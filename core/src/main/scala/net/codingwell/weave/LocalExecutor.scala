// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject.Inject
import akka.actor._

class LocalExecutor @Inject() (val compilers:java.util.Set[Compiler]) extends Actor {

  def receive = {
    case s:String =>
      println("Msg: " + s);
    case unknown =>
      println(this.toString() + " recieved unexpected message.")
  }
}
