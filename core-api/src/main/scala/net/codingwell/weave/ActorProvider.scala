// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.common.collect.ImmutableSet
import com.google.inject._
import com.google.inject.spi._
import akka.actor._

class ActorProvider[T <: Actor] @Inject() ( val m:TypeLiteral[T], val system:ActorSystem, val injector:Injector ) extends ProviderWithDependencies[ActorRef] {
  def get = {
    system.actorOf( Props( injector.getInstance( Key.get( m ) ) ) )
  }

  def getDependencies() = {
    ImmutableSet.of( Dependency.get( Key.get( m ) ) )
  }
}
