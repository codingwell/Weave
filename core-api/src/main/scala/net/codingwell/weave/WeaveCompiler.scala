// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import scala.actors._
import scala.collection.JavaConversions._
import scala.collection.mutable.{Set => MutableSet}

/*
abstract class Executor {
   def compile( file:WeaveFile ) = {}
}
*/

object WeaveActor {
  case class QueueFile( file:WeaveFile )
}

class WeaveActor extends Actor {
  def act() {
    loop {
      react {
        case _ => println(this.toString() + " recieved unexpected message.")
      }
    }
  }
}

class WeaveCompiler @Inject() ( @Executor() val engines:java.util.Set[Actor] ) {
  
  def compile( files:Seq[WeaveFile] ):Unit = {
    val nativeengines:MutableSet[Actor] = engines
    nativeengines foreach (_ ! "Message!")
    nativeengines foreach (_ ! "Message!")
    nativeengines foreach (_ ! 1)
  }
}
