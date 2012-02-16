// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

class ASTRTLVisitor(val symbols:SymbolTable) {

  var currentScope:Option[SymbolScope] = None

  def pushScope() = currentScope = Some(new SymbolScope( currentScope ))
  def popScope() = currentScope match { case Some(scope) => currentScope = scope.parent case None => None }
  def subScope( body : => Unit ) = { pushScope; body; popScope }
  def getScope() = currentScope match { case Some(scope) => scope case None => throw new Exception("Missing Scope") }

  def visit( obj:AnyRef ):Unit = {
    obj match {
      case ast.File( globalStatements ) =>
        subScope {
          println( "-File" )
          globalStatements foreach ( visit _ )
        }
      case ast.Import( packageSpecification ) =>
        getScope().locator.add()
      case ast.ImportViral( packageSpecification ) =>
        getScope().locator.add()
      case ast.Module( identifier, parameters, scope ) =>
        println( "+-Module:" + identifier.toString )
        subScope {
          visit( scope )
        }
      case ast.Scope( statements ) =>
        subScope {
          println( "  >Scope" )
          statements foreach ( visit _ )
        }
      case ast.ExpressionStatement( body ) =>
        println( "   ExpressionStatement" )
      case unknown =>
        println("Unknown ast member: " + unknown.toString )
    }
  }
}
