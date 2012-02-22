// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import scala.collection.{ mutable => mu }
import net.codingwell.weave.languages.silk.exceptions._

class SymbolLocator {
  def add():Unit = {
    //Todo
  }
}

class PackageRef {
}

class Symbol {
}

class ModuleParameters() {
  val orderedParameters = new mu.ArrayBuffer[Connection]
  val namedParameters = new mu.HashMap[String,Connection]

  def appendParameter( name:String, connection:Connection ) = {
    orderedParameters += connection
    namedParameters += ( ( name, connection ) )
  }
}

case class ModuleSymbol( val name:String, val parameters:ModuleParameters, val scope:SymbolScope ) extends Symbol {
}
case class DeclarationSymbol( val connection:Connection ) extends Symbol {
}
case class TypeSymbol() extends Symbol {
}

class SymbolScope( val parent:Option[SymbolScope] = None ) {
  val packages = new mu.ArrayBuffer[PackageRef]
  val symbols = new mu.HashMap[String,Symbol]

  def addSymbol( name:String, symbol:Symbol ) = {
    symbols get( name ) match {
      case Some( oldsymbol ) => throw DuplicateSymbolException( name, symbol, oldsymbol )
      case None => symbols += ( (name, symbol) )
    }
  }
}

class SymbolTable {
  var scope:Option[SymbolScope] = None
  val modules = new mu.ArrayBuffer[ModuleSymbol]
}

//Do not use, for later expansion
/*
object SymbolTable {
  class Package() {
    val children = new MutableHashMap[String,Package]
    val objects  = new MutableHashMap[String,String]//FIXME, shouldn't be a string, should be something else

    def $( symbol:String ):Package = {
      children get symbol match {
        case Some(node) =>
          node
        case _ =>
          val newchild = new Package
          children put( symbol, newchild )
          newchild
      }
    }

    def set( symbol:String, obj:String ) = {
      objects put( symbol, obj )
    }
  }
}

class SymbolTable {

}
*/
