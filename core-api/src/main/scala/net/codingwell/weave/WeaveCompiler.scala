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
import scala.collection.JavaConversions._
import scala.collection.mutable.{Set => MutableSet, Queue => MutableQueue}

object WeaveActor {
  case class QueueFile( file:WeaveFile )
  case object Quit
}
import WeaveActor._

class WeaveActor ( val engines:MutableSet[ActorRef] ) extends Actor {
  @Inject() def this( @Named("Executors") engines:java.util.Set[ActorRef] ) = this( asScalaSet(engines) )

  val fileQueue = new MutableQueue[WeaveFile]

  def receive = {
    case QueueFile(file) =>
      fileQueue enqueue file
    case Quit =>
      self ! akka.actor.PoisonPill
    case unknown =>
      println(this.toString() + " recieved unexpected message.")
  }

  override def postStop() = {
      engines foreach ( _ ! akka.actor.PoisonPill )
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
    weaveActor ! WeaveActor.QueueFile( new NativeWeaveFile( new File("samples/test2.silk"), "silk") )
    weaveActor ! WeaveActor.Quit
  }
}
