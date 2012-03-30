// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import com.google.inject.name._
import java.io.File
import akka.actor._
import akka.util.Timeout
import akka.util.duration._
import akka.pattern.{ ask, pipe }
import akka.pattern.{ ask, pipe }
import akka.dispatch.Await
import scala.collection.JavaConversions._
import scala.collection.{ mutable => mu, immutable => im }

object WeaveCompiler {
  //Incoming
  case class NotifyWork( val actor:ActorRef, val source:String, target:String )

  //Outgoing
  case class RequestWork(val source:String, val target:String)
  case class WorkCompleted[S](val value:S, val modules:im.Vector[ModuleSymbol] )
  
  case class Work[S](val value:S)

  //case class Compile( file:WeaveFile )
  //case object GetSupportedLanguages
}
object WeaveActor {
  case class QueueFile( file:WeaveFile )
  case object Join
  case object Quit
}
import WeaveActor._

trait GeneratorVisitor {
  def generate( toplevel:ModuleSymbol ):Unit
}

class WeaveActor @Inject() ( @Named("Executors") val executors:im.Set[ActorRef], val generator:GeneratorVisitor ) extends Actor {

  class Work (val channel:ActorRef, val file:WeaveFile, val engine:ActorRef) {
  }

  val pendingQueue = new mu.Queue[WeaveFile]
  val workingQueue = new mu.Queue[WeaveFile]
  val doneQueue = new mu.Queue[WeaveFile]
  val notifyWhenDone = new mu.HashSet[ActorRef]

  var modules = im.Vector[ModuleSymbol]()

  def receive = {
    case QueueFile(file) =>
      pendingQueue enqueue file
      executors foreach ( _ ! WeaveCompiler.NotifyWork(self, "file",file.filetype) )
    case WeaveCompiler.RequestWork(source,target) =>
      if( !pendingQueue.isEmpty )
      {
        val item = ( pendingQueue dequeue )
        workingQueue enqueue item
        sender ! WeaveCompiler.Work( item )
      }
      else
        sender ! null
    case WeaveCompiler.WorkCompleted( value, modules ) =>
      println("Work Done.")
      workingQueue dequeueFirst ( value == _ ) match {
        case Some( item ) =>
          doneQueue enqueue item
          this.modules = this.modules ++ modules
        case None =>
          println("Work was not in progress. " + value.toString )
      }

      if( pendingQueue.isEmpty && workingQueue.isEmpty )
      {
        println("Done Parsing.")
        println("Starting Generation")

        //FIXME: Find toplevel
        generator.generate( modules.head )

        notifyWhenDone foreach ( _ ! 'Done )
      }
    case Join =>
      notifyWhenDone += sender
    case unknown =>
      println(this.toString() + " recieved unexpected message.")
  }

  override def postStop() = {
      executors foreach ( _ ! akka.actor.PoisonPill )
  }
}

case class WeaveModule() extends AbstractModule {

  def configure() = {
    bind(classOf[ActorRef])
      .annotatedWith( Names.named("WeaveActor") )
      .toProvider( new TypeLiteral[ActorProvider[WeaveActor]]() {} )
  }

}

class WeaveCompiler @Inject() ( @Named("WeaveActor") val weaveActor:ActorRef ) {

  def compile( files:Seq[WeaveFile] ):Unit = {
    implicit val timeout = Timeout(5 seconds)
    weaveActor ! WeaveActor.QueueFile( new NativeWeaveFile( new File("../samples/test3.silk"), "silk") )

    val future = weaveActor ? WeaveActor.Join

    try {
      val result = Await.result( future, timeout.duration ).asInstanceOf[Any]
    }
    catch {
      case unknown =>
        println( "Exception while waiting: " + unknown )
    }

  }
}
