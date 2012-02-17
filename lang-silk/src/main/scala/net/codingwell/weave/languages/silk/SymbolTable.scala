// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import scala.collection.mutable.{HashMap => MutableHashMap, LinkedList => MutableLinkedList}

class SymbolLocator {
  def add():Unit = {
    //Todo
  }
}

class PackageRef {
}

class Symbol {
}

case class ModuleSymbol() extends Symbol {
}
case class DeclarationSymbol() extends Symbol {
}
case class TypeSymbol() extends Symbol {
}

class SymbolScope( val parent:Option[SymbolScope] = None ) {
  val packages = new MutableLinkedList[PackageRef]
  val symbols = new MutableHashMap[String,Symbol]
}

class SymbolTable {
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
