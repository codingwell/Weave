// Copyright (c) 2012 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.verilog

import net.codingwell.weave._
import scala.collection.{ mutable => mu }

class SymbolTable () {

  var identifierSalt:Int = 0

  def generateIdentifier():String = {
    
    val id = new mu.StringBuilder
    var count = identifierSalt
    do {
      val mod = count % 26
      count = count / 26
      val ch = ('a' + mod).toChar
      id append ch
    } while ( count > 0 )
    
    identifierSalt = identifierSalt + 1

    "_$_WEAVE_$__GEN__" + id.toString
  }
}

class VerilogGeneratorVisitor() extends GeneratorVisitor {

  def generate( toplevel:ModuleSymbol ):Unit = {
    val symbolTable = new SymbolTable

    println("Generating TopLevel: " + toplevel.name)

    toplevel.parameters.namedParameters foreach { case (k,v) => {
      println("Parameter: " + k)
      if( v.isDriven ) {
        processOutput( v, symbolTable )
      }
    }}
  }

  def processOutput( connection:Connection, symbolTable:SymbolTable ) = {
    handleSignal( connection, symbolTable )
  }

  def handleSignal( connectionSignal:ConnectionSignal, symbolTable:SymbolTable ):Unit = {
    connectionSignal match {
      case connection @ Connection() =>
        println("Connection assigned symbol: " + symbolTable.generateIdentifier)

        connection.input match {
          case Some( signal ) =>
            handleSignal( signal, symbolTable )
          case None =>
            println( "ERR: Connection has no signal!" )
        }
      case unknown =>
        println("Unknown ConnectionSignal: " + unknown)
    }
  }

}
