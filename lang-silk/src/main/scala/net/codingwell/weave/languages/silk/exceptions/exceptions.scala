// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk.exceptions

import net.codingwell.weave.languages.silk._
import net.codingwell.weave.Symbol

case class DuplicateSymbolException( name:String, newsymbol:Symbol, oldsymbol:Symbol ) extends Exception
case class InvalidDirectionException( direction:String ) extends Exception
