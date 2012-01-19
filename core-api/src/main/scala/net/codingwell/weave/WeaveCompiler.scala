// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._

//FIXME: Placeholder
sealed abstract class Engine

class WeaveCompiler @Inject() ( val engines:Seq[Engine] ) {
   
}
