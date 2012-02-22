// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import net.codingwell.weave.languages.silk.exceptions._

class ASTRTLVisitor(val symbols:SymbolTable) {

  def pushScope() = symbols.scope = Some(new SymbolScope( symbols.scope ))
  def popScope() = symbols.scope match { case Some(scope) => symbols.scope = scope.parent case None => None }
  def subScope( body : => Unit ) = { pushScope; body; popScope }
  def getScope() = symbols.scope match { case Some(scope) => scope case None => throw new Exception("Missing Scope") }
  def addModule( module:ModuleSymbol ) = {symbols.modules += module }

  def visit( obj:AnyRef ):Unit = {
    obj match {
      case ast.File( globalStatements ) =>
        subScope {
          println( "-File" )
          globalStatements foreach ( visit _ )
        }
      case ast.Import( packageSpecification ) =>
        getScope().packages :+ new PackageRef
      case ast.ImportViral( packageSpecification ) =>
        getScope().packages :+ new PackageRef
      case ast.Module( identifier, parameters, scope ) =>
        println( "+-Module:" + identifier.toString )
        val parentScope = getScope()
        subScope {
          val moduleparameters = new ModuleParameters()
          parameters foreach ( handleParameter( _, moduleparameters ) )
          val module = new ModuleSymbol( identifier.name, moduleparameters, getScope() )
          parentScope.addSymbol( identifier.name, module )
          addModule( module )

          handleScope( scope )
        }
      case scope @ ast.Scope( statements ) =>
        subScope {
          handleScope( scope )
        }
      case ast.Instantiation( typename, identifier ) =>
        println( "   Instantiation " + identifier.name )
        val decl = new DeclarationSymbol( new Connection() )
        getScope().addSymbol( identifier.name, decl )
      case ast.ExpressionStatement( body ) =>
        println( "   ExpressionStatement" )
        body.expressions foreach ( visit _ )
      case ast.Expression( simple, ochain ) =>
        visit( simple )
        ochain map ( visit _ )
      case ast.Identifier( name ) =>
        println( "   id: " + name )
      case unknown =>
        println("Unknown ast member: " + unknown.toString )
    }
  }

  def handleScope( scope:ast.Scope ):Unit = {
    scope.statements foreach ( visit _ )
  }
  
  def handleParameter( parameter:ast.Parameter, moduleparameters:ModuleParameters ):Unit = {

    //We use this to enforce the directionality
    val connectionin = new Connection()
    val connectionout = new Connection()
    connectionout.connectSignal( connectionin )
    val (moduleconnection, scopeconnection) = parameter.direction.value match {
      case "in"    =>
        ( connectionin, connectionout )
      case "out"   =>
        ( connectionout, connectionin )
      case unknown =>
        throw InvalidDirectionException(unknown)
    }

    getScope().addSymbol( parameter.identifier.name, DeclarationSymbol( scopeconnection ) )
    moduleparameters.appendParameter( parameter.identifier.name, moduleconnection )
  }
}
