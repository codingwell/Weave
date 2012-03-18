// Copyright (c) 2012 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject.Inject
import com.google.inject.name._
import scala.collection.JavaConversions._
import scala.collection.{mutable => mu, immutable => im}
import akka.actor._

class LocalExecutor @Inject() ( @Named("LangCompilers") val compilers:im.Set[ActorRef] ) extends Actor {
  def receive = {
    case msg @ WeaveCompiler.NotifyWork(actor,source,target) =>
      //println("Msg: " + source + target)
      compilers foreach ( _.forward(msg) )
    case unknown =>
      println(this.toString() + " recieved unexpected message.")
  }

  override def postStop() = {
    compilers foreach ( _ ! PoisonPill )
  }
}
