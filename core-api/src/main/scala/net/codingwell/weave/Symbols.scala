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

  def isDriven() = { ! input.isEmpty }
}

class ModuleParameters() {
  val orderedParameters = new mu.ArrayBuffer[Connection]
  val namedParameters = new mu.HashMap[String,Connection]

  def appendParameter( name:String, connection:Connection ) = {
    orderedParameters += connection
    namedParameters += ( ( name, connection ) )
  }
}

case class ModuleSymbol( val name:String, val parameters:ModuleParameters ) extends Symbol {}
