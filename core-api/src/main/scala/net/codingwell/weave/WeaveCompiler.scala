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
import scala.collection.JavaConversions._
import scala.collection.mutable.{Set => MutableSet, Queue => MutableQueue, HashSet => MutableHashSet}

object WeaveCompiler {
  //Incoming
  case class NotifyWork( val actor:ActorRef, val source:String, target:String )

  //Outgoing
  case class RequestWork(val source:String, val target:String)
  
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

class WeaveActor ( val executors:MutableSet[ActorRef] ) extends Actor {
  @Inject() def this( @Named("Executors") executors:java.util.Set[ActorRef] ) = this( asScalaSet(executors) )

  class Work (val channel:ActorRef, val file:WeaveFile, val engine:ActorRef) {
  }

  val pendingQueue = new MutableQueue[WeaveFile]
  val workingQueue = new MutableQueue[Work]
  val doneQueue = new MutableQueue[WeaveFile]
  val notifyWhenDone = new MutableHashSet[ActorRef]

  def receive = {
    case QueueFile(file) =>
      pendingQueue enqueue file
      executors foreach ( _ ! WeaveCompiler.NotifyWork(self, "file",file.filetype) )
      executors foreach ( _ ! WeaveCompiler.NotifyWork(self, "file",file.filetype) )
      sender ! 'Fail
    case WeaveCompiler.RequestWork(source,target) =>
      if( !pendingQueue.isEmpty )
        sender ! WeaveCompiler.Work( pendingQueue dequeue )
      else
        sender ! null
    case Quit =>
      self ! akka.actor.PoisonPill
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
  val future = weaveActor ? WeaveActor.QueueFile( new NativeWeaveFile( new File("../samples/test3.silk"), "silk") )

  future onSuccess {
      case Some('Done) => println("YAY")
      case Some('Fail) => println("x.x")
      case None => println(":(")
      case _ => println("???")
    }

    //FIXME
    Thread.sleep(5000);
  }
}
