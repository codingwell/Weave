// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import scala.collection.{ mutable => mu }
import exceptions._

class Symbol { }

class ConnectionType { }

trait ConnectionSignal {
  var signaltype:Option[ConnectionType] = None

  def doSomething():Unit = {
  }
}

/**
 * \brief This represents a logical wire.
 *
 * Note that it only refers to the signal and not the reciever. This is becuase the RTL is a tree steming from outputs and working back to
 * inputs.
*/
case class Connection () extends ConnectionSignal {
  var input :Option[ConnectionSignal] = None

  def connectSignal( signal:ConnectionSignal ) = {
    input match {
      case Some( oldsignal ) =>
        throw MultipleDriverException( this, signal, oldsignal )
      case None =>
        input = Some(signal)
    }
  }

  def isDriven() = { ! input.isEmpty }
}

case class ModuleInput () extends ConnectionSignal {}

class ModuleInstance ( module:ModuleSymbol ) {
//TODO: The key should really be a ModuleInput, but we currently don't have access to that type without extending ModuleParameter to be
//direction specific
  val inputs = new mu.HashMap[ConnectionSignal,ConnectionSignal]
}

class ModuleConnection( instance:ModuleInstance, instanceConnection:ConnectionSignal ) extends ConnectionSignal {

}

case class ModuleParameter( direction:String, signal:ConnectionSignal )

class ModuleParameters() {
  val orderedParameters = new mu.ArrayBuffer[ModuleParameter]
  val namedParameters = new mu.HashMap[String,ModuleParameter]

  def appendParameter( name:String, parameter:ModuleParameter ) = {
    orderedParameters += parameter
    namedParameters += ( ( name, parameter ) )
  }
}

case class ModuleSymbol( val name:String, val parameters:ModuleParameters ) extends Symbol {}
