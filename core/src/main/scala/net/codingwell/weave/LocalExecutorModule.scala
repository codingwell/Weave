// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import com.google.inject.multibindings._
import scala.actors._


case class LocalExecutorModule() extends AbstractModule {

  def configure() = {
    val executorbinder = Multibinder.newSetBinder( binder(), classOf[Actor], classOf[Executor] )

    executorbinder.addBinding().to(classOf[LocalExecutor])
  }

}

// vim: tabstop=2 shiftwidth=2