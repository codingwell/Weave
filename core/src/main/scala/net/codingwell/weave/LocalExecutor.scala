// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject.Inject
import com.google.inject.name._
import scala.collection.JavaConversions._
import scala.collection.mutable.{Set => MutableSet}
import akka.actor._

class LocalExecutor ( val compilers:MutableSet[ActorRef]) extends Actor {
  @Inject() def this( @Named("LangCompilers") compilers:java.util.Set[ActorRef] ) = this( asScalaSet(compilers) )

  override def preStart() = {
    compilers foreach ( self.link( _ ) ) //This doesn't cause engines to exit when self takes the PoisonPill
  }
  
  def receive = {
    case s:String =>
      println("Msg: " + s);
    case unknown =>
      println(this.toString() + " recieved unexpected message.")
  }
}
