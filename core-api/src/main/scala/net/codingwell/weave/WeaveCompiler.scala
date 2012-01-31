// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._

//@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
//public @interface Executor {}

abstract class Executor {
   def compile( file:WeaveFile ) = {}
}

class WeaveCompiler @Inject() ( val engines:java.util.Set[Executor] ) {
  def compile( files:Seq[WeaveFile] ):Unit = { }
}
