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

trait ConnectionSignal extends Equals {
  var signaltype:Option[ConnectionType] = None

  def doSomething():Unit = {
  }

  override def hashCode = 41 + signaltype.hashCode
  override def canEqual( other:Any ) = other.isInstanceOf[ConnectionSignal]
/*  override def equals( other:Any ) = other match {
    case that: ConnectionSignal =>
      (that canEqual this) &&
        signaltype == that.signaltype
    case _ =>
      false
  }*/
}
case class Gate_XOR ( val a:ConnectionSignal, val b:ConnectionSignal ) extends ConnectionSignal { }
case class Gate_AND ( val a:ConnectionSignal, val b:ConnectionSignal ) extends ConnectionSignal { }
case class Gate_OR  ( val a:ConnectionSignal, val b:ConnectionSignal ) extends ConnectionSignal { }

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

  override def hashCode = 41 * (
                            41 + super.hashCode
                          ) + input.hashCode
  override def canEqual( other:Any ) = other.isInstanceOf[Connection]
  override def equals( other:Any ) = other match {
    case that: Connection =>
      super.equals( that ) &&
        input == that.input
    case _ =>
      false
  }
}

case class ModuleInput ( val name:String ) extends ConnectionSignal {}

final class ModuleInstance ( val module:ModuleSymbol ) {
//TODO: The key should really be a ModuleInput, but we currently don't have access to that type without extending ModuleParameter to be
//direction specific
  val inputs = new mu.HashMap[ConnectionSignal,ConnectionSignal]

  override def hashCode = 41 * (
                            41 + module.hashCode
                          ) + inputs.hashCode
  override def equals( other:Any ) = other match {
    case that: ModuleInstance =>
        module == that.module &&
        inputs == that.inputs
    case _ =>
      false
  }
}

case class ModuleConnection( instance:ModuleInstance, instanceConnection:ConnectionSignal ) extends ConnectionSignal {}

case class ModuleParameter( name:String, direction:String, signal:ConnectionSignal )

class ModuleParameters() {
  val orderedParameters = new mu.ArrayBuffer[ModuleParameter]
  val namedParameters = new mu.HashMap[String,ModuleParameter]

  def appendParameter( parameter:ModuleParameter ) = {
    orderedParameters += parameter
    namedParameters += ( ( parameter.name, parameter ) )
  }
}

case class ModuleSymbol( val name:String, val parameters:ModuleParameters ) extends Symbol {}
