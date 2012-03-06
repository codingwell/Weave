// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import net.codingwell.weave.languages.silk.exceptions._
import net.codingwell.weave._

class SilkType() {}
/*
trait ConnectionSignal {
  var signaltype:Option[SilkType] = None

  def doSomething():Unit = {
  }
}
*/
/**
 * \brief This represents a logical wire.
 *
 * Note that it only refers to the signal and not the reciever. This is becuase the RTL is a tree steming from outputs and working back to
 * inputs.
*//*
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
}*/

class ModuleInstance ( module:ModuleSymbol, lhs:ConnectionSignal, rhs:ConnectionSignal ) extends ConnectionSignal {}

class TemporaryConnection () extends ConnectionSignal {}

class Gate_XOR ( val a:ConnectionSignal, val b:ConnectionSignal ) extends ConnectionSignal { }
class Gate_AND ( val a:ConnectionSignal, val b:ConnectionSignal ) extends ConnectionSignal { }
class Gate_OR  ( val a:ConnectionSignal, val b:ConnectionSignal ) extends ConnectionSignal { }

trait ExpressionState
{
  def processExpression( expression:ast.Expression ):Unit

  def getValue():ConnectionSignal
}

class ExpressionModuleState( val machine:ExpressionStateMachine, val value:ConnectionSignal ) extends ExpressionState {
  def processExpression( expression:ast.Expression ) = {
    expression.next match {
      case None =>
        expression.simple match {
          case ast.Identifier( name ) =>
            println( "Value State ID: " + name )

            val symbol = machine.scope.lookup( name )

            symbol match {
              case Some( DeclarationSymbol( connection ) ) =>
                throw new Exception("Value followed by value")
              case Some( symbol @ ModuleSymbol( name, parameters ) ) =>
                //TODO: Handle parameters
                println( "ID is Module in Module State" )
                machine.state = new ExpressionModuleHalfState( machine, symbol, value )
              case None =>
                println( "No ID" )
            }


          case ast.ExpressionGroup( expressions ) =>
            throw new Exception("Module expected got group")
          case unknown =>
            println( "Value State Unknown: " + unknown.toString() )
        }
      case _ =>
        println("Chain Expressions not implemented." )
    }
  }

  def getValue() = { value }
}

class ExpressionModuleHalfState( val machine:ExpressionStateMachine, val module:ModuleSymbol, val rhs:ConnectionSignal ) extends ExpressionState {
  def processExpression( expression:ast.Expression ) = {
    expression.next match {
      case None =>
        expression.simple match {
          case ast.Identifier( name ) =>
            println( "Value State ID: " + name )

            val symbol = machine.scope.lookup( name )

            symbol match {
              case Some( DeclarationSymbol( connection ) ) =>
                println( "ID is Value in HalfState" )
                machine.state = new ExpressionModuleState( machine, new ModuleInstance( module, connection, rhs ) )
              case Some( ModuleSymbol( name, parameters ) ) =>
                println( "ID is Module" )
                throw new Exception("Value expected got module")
              case None =>
                println( "No ID" )
            }


          case ast.ExpressionGroup( expressions ) =>

            println( "Value State Group: " + expressions.toString() )
            val submachine = new ExpressionStateMachine( machine.scope, machine.table )
            expressions.reverseIterator foreach ( submachine.processExpression _ )
            
            machine.state = new ExpressionModuleState( machine, new ModuleInstance( module, submachine.getValue, rhs ) )

          case unknown =>
            println( "Value State Unknown: " + unknown.toString() )
        }
      case _ =>
        println("Chain Expressions not implemented." )
    }
  }

  def getValue():ConnectionSignal = { throw new Exception("Module missing left-hand-side") }
}

class ExpressionValueState( val machine:ExpressionStateMachine, val value:ConnectionSignal ) extends ExpressionState {
  def processExpression( expression:ast.Expression ) = {
    expression.next match {
      case None =>
        expression.simple match {
          case ast.Identifier( name ) =>
            println( "Value State ID: " + name )

            val symbol = machine.scope.lookup( name )

            symbol match {
              case Some( DeclarationSymbol( connection ) ) =>
                throw new Exception("Value followed by value")
              case Some( symbol @ ModuleSymbol( name, parameters ) ) =>
                //TODO: Handle parameters
                println( "ID is Module" )
                machine.state = new ExpressionModuleHalfState( machine, symbol, value )
              case None =>
                println( "No ID" )
            }


          case ast.ExpressionGroup( expressions ) =>
            throw new Exception("Module expected got group")
          case unknown =>
            println( "Value State Unknown: " + unknown.toString() )
        }
      case _ =>
        println("Chain Expressions not implemented." )
    }
  }

  def getValue():ConnectionSignal = { value }
}

class ExpressionValueEmptyState( val machine:ExpressionStateMachine ) extends ExpressionState {
  def processExpression( expression:ast.Expression ) = {
    expression.next match {
      case None =>
        expression.simple match {
          case ast.Identifier( name ) =>
            println( "Value State ID: " + name )

            //Todo: Lookup identifier
            val symbol = machine.scope.lookup( name )

            symbol match {
              case Some( DeclarationSymbol( connection ) ) =>
                machine.state = new ExpressionValueState( machine, connection )
              case Some( ModuleSymbol( name, parameters ) ) =>
                println( "ID is Module" )
                throw new Exception("Value expected got module")
              case None =>
                println( "No ID" )
            }


          case ast.ExpressionGroup( expressions ) =>

            println( "(" )
            val submachine = new ExpressionStateMachine( machine.scope, machine.table )
            expressions.reverseIterator foreach ( submachine.processExpression _ )
            println( ")" )

            machine.state = new ExpressionValueState( machine, submachine.getValue )
          case unknown =>
            println( "Value State Unknown: " + unknown.toString() )
        }
      case _ =>
        println("Chain Expressions not implemented." )
    }
  }

  def getValue() = { throw new Exception("Missing Value") }
}

class ExpressionStateMachine( val scope:SymbolScope, val table:SymbolTable ) {
  var state:ExpressionState = new ExpressionValueEmptyState( this )
  
  def processExpression( expression:ast.Expression ):Unit = { state.processExpression( expression ) }

  def getValue():ConnectionSignal = { state.getValue }
}

class SemanticModule(  ) {
}

class Semantic ( table:SymbolTable ) {
  def process():Unit = {
    table.modulespending foreach { processModule _ }
  }

  def processModule( module:ModulePendingSymbolics ):Unit = {
    println( "Processing module: " ) //+ module.name )
    module.statements foreach ( processStatement _ )
  }

  def processStatement( semanticstatement:SemanticStatement ):Unit = {
    val statement = semanticstatement.statement
    val scope = semanticstatement.scope

    statement match {
      case ast.ExpressionStatement( group ) =>
        val expressions = group.expressions
        println("Expression Group: " + expressions.toString )

        val machine = new ExpressionStateMachine( scope, table )
        expressions.reverseIterator foreach ( machine.processExpression _ )
        //expressions.reverseIterator foreach ( processExpression(scope) _ )
      case unknown =>
        println("Unknown semantic statement: " + unknown.toString )
    }
  }

  def processExpression( scope:SymbolScope )( expression:ast.Expression ):Unit = {
    expression.next match {
      case None =>
        expression.simple match {
          case ast.Identifier( name ) =>
            val symbol = scope.lookup( name )
            println( "Simple Identifier: " + name + " " + symbol )
          case ast.ExpressionGroup( expressions ) =>
            println( "(" )
            expressions foreach ( processExpression(scope) _ )
            println( ")" )
          case unknown =>
            println("Unknown simple expression: " + unknown.toString )
        }
      case _ =>
        println("Chain Expressions not implemented." )
    }
  }
}