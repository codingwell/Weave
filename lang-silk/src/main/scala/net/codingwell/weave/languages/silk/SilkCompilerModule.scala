// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import net.codingwell.weave.Compiler

import com.google.inject._
import com.google.inject.multibindings._

case class SilkCompilerModule() extends AbstractModule {

  def configure() = {
    val compilerbinder = Multibinder.newSetBinder( binder(), classOf[Compiler] )

    compilerbinder.addBinding().to(classOf[SilkCompiler])
  }

}

// vim: tabstop=2 shiftwidth=2
