// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import scala.collection.{ mutable => mu, immutable => im }
import net.codingwell.weave.languages.silk.exceptions._
import net.codingwell.weave._

class SemanticStatement ( val scope:SymbolScope, val statement:ast.Statement ) {}

class SymbolLocator {
  def add():Unit = {
    //Todo
  }
}

class PackageRef {
}
/*
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
*/

case class ModulePendingSymbolics() {
  val statements = new mu.ArrayBuffer[SemanticStatement]
}

/*
case class ModuleSymbol( val name:String, val parameters:ModuleParameters ) extends Symbol {
}
*/
object built_in {
  def get_&():ModuleSymbol = {
    val a = new Connection
    val b = new Connection
    val result = new Connection
    result.connectSignal( new Gate_AND( a, b ) )

    val parameters = new ModuleParameters
    parameters.appendParameter( "a", ModuleParameter("in", a) )
    parameters.appendParameter( "b", ModuleParameter("in", b) )
    parameters.appendParameter( "result", ModuleParameter("ret", result) )

    new ModuleSymbol( "&", parameters )
  }
  def get_|():ModuleSymbol = {
    val a = new Connection
    val b = new Connection
    val result = new Connection
    result.connectSignal( new Gate_OR( a, b ) )

    val parameters = new ModuleParameters
    parameters.appendParameter( "a", ModuleParameter("in", a) )
    parameters.appendParameter( "b", ModuleParameter("in", b) )
    parameters.appendParameter( "result", ModuleParameter("ret", result) )

    new ModuleSymbol( "|", parameters )
  }
  def get_^():ModuleSymbol = {
    val a = new Connection
    val b = new Connection
    val result = new Connection
    result.connectSignal( new Gate_XOR( a, b ) )

    val parameters = new ModuleParameters
    parameters.appendParameter( "a", ModuleParameter("in", a) )
    parameters.appendParameter( "b", ModuleParameter("in", b) )
    parameters.appendParameter( "result", ModuleParameter("ret", result) )

    new ModuleSymbol( "^", parameters )
  }
  def get_=():ModuleSymbol = {
    val a = new Connection
    val b = new Connection

    a.connectSignal( b )

    val parameters = new ModuleParameters
    parameters.appendParameter( "a", ModuleParameter("out", a) )
    parameters.appendParameter( "b", ModuleParameter("in", b) )
    parameters.appendParameter( "result", ModuleParameter("ret", a) )

    new ModuleSymbol( "=", parameters )
  }
}

//This isn't right, but right concept
//Maybe move the statements into another class, we don't really seem to ever refer to the module from them anyway.
class AssignmentModuleSymbol( parameters:ModuleParameters ) extends ModuleSymbol( "", parameters )

case class DeclarationSymbol( val connection:Connection ) extends Symbol {
}
case class TypeSymbol() extends Symbol {
}

class DefaultSymbolScope() extends SymbolScope( None ) {
  addSymbol( "^", built_in.get_^ )
  addSymbol( "|", built_in.get_| )
  addSymbol( "&", built_in.get_& )
  addSymbol( "=", built_in.get_= )
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

  def lookup( name:String ):Option[Symbol] = {
    symbols get name match {
      case value @ Some( symbol ) =>
        value
      case None =>
        parent match {
          case Some( scope ) =>
            scope lookup name
          case None =>
            None
        }
    }
  }
}

//USe imutable vector
class SymbolTable {
  var scope:Option[SymbolScope] = Some( new DefaultSymbolScope )
  val modules = new mu.ArrayBuffer[ModuleSymbol]
  val modulespending = new mu.ArrayBuffer[ModulePendingSymbolics]

  def pushScope() = scope = Some(new SymbolScope( scope ))
  def popScope() = scope match { case Some(thisscope) => scope = thisscope.parent case None => None }
  def subScope( body : => Unit ) = { pushScope; body; popScope }
  def getScope() = scope match { case Some(thisscope) => thisscope case None => throw new Exception("Missing Scope") }
  def addModule( module:ModuleSymbol, modulepending:ModulePendingSymbolics ) = { modules += module; modulespending += modulepending }

  def getImmutableModules() = { im.Vector( modules: _* ) }
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
