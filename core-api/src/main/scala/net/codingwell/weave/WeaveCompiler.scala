// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import com.google.inject.name._
import akka.actor._
import scala.collection.JavaConversions._
import scala.collection.mutable.{Set => MutableSet, Queue => MutableQueue}

sealed trait AExecutorMessage
case class AQueueFile( file:WeaveFile ) extends AExecutorMessage
case object AQuit extends AExecutorMessage

class Master extends akka.actor.Actor {
def receive = {
  case AQuit =>
      self ! akka.actor.PoisonPill
}}
object WeaveActor {
  case class QueueFile( file:WeaveFile )
  case object Quit
}
import WeaveActor._

class WeaveActor ( val engines:MutableSet[ActorRef] ) extends Actor {
  @Inject() def this( @Named("Executors") engines:java.util.Set[ActorRef] ) = this( asScalaSet(engines) )

  val fileQueue = new MutableQueue[WeaveFile]

  override def preStart() = {
    engines foreach ( self.link( _ ) ) //This doesn't cause engines to exit when self takes the PoisonPill
  }

  def receive = {
    case QueueFile(file) =>
      fileQueue enqueue file
    case Quit =>
      println("You should have taken the blue pill.");
      self.stop()
//      self ! akka.actor.PoisonPill
//      engines foreach ( _ ! akka.actor.PoisonPill ) //This does cause engines to exit when self takes the PoisonPill
    case unknown =>
      println(this.toString() + " recieved unexpected message.")
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
//    nativeengines foreach (_ ! "Message!")
 //   nativeengines foreach (_ ! "Message!")
 //   nativeengines foreach (_ ! 1)
    Thread.sleep(3000)
    weaveActor ! WeaveActor.Quit
  }
}
