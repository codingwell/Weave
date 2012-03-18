// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import com.google.inject.name._
import com.google.inject.multibindings._
import akka.actor._

import uk.me.lings.scalaguice._

case class LocalExecutorModule() extends AbstractModule {

  def configure() = {
    val executorbinder = ScalaMultibinder.newSetBinder( binder(), classOf[ActorRef], Names.named("Executors") )

    executorbinder.addBinding().toProvider( new TypeLiteral[ActorProvider[LocalExecutor]]() {} )
  }

}

// vim: tabstop=2 shiftwidth=2
