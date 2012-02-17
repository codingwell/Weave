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
        getScope().packages :+ new PackageRef
      case ast.ImportViral( packageSpecification ) =>
        getScope().packages :+ new PackageRef
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
      case ast.Instantiation( typename, name ) =>
        println( "   Instantiation " + name.name )
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
}
