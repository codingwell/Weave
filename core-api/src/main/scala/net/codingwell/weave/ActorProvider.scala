// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import akka.actor._

class ActorProvider[T <: Actor] @Inject() ( val m:TypeLiteral[T], val injector:Injector ) extends Provider[ActorRef] {
  def get = {
    Actor.actorOf( injector.getInstance( Key.get( m ) ) ).start
  }
}
