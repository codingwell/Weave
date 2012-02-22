// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import net.codingwell.weave.languages.silk.exceptions._

class SilkType() {}

trait ConnectionSignal {
  var signaltype:Option[SilkType] = None

  def doSomething():Unit = {
  }
}

/**
 * \brief This represents a logical wire.
 *
 * Note that it only refers to the signal and not the reciever. This is becuase the RTL is a tree steming from outputs and working back to
 * inputs.
*/
class Connection () extends ConnectionSignal {
  var input :Option[ConnectionSignal] = None

  def connectSignal( signal:ConnectionSignal ) = {
    input match {
      case Some( oldsignal ) =>
        throw MultipleDriverException( this, signal, oldsignal )
      case None =>
        input = Some(signal)
    }
  }
}

class ModuleRTL (  ) {

}

class Semantic ( table:SymbolTable ) {
  def process():Unit = {
    table.modules foreach { processModule _ }
  }

  def processModule( module:ModuleSymbol ):Unit = {
    println( "Processing module: " + module.name )
  }
}
