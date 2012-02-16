// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk.ast

case class File( val members:Seq[GlobalStatement])

sealed abstract class Statement

class GlobalStatement() extends Statement {}

//case class Module( val identifier:Identifier ) extends GlobalStatement {}
case class Module( val identifier:Identifier, val parameters:Seq[Parameter] , val scope:Scope ) extends GlobalStatement {}
class ImportStatement( val packagespec:PackageSpecification ) extends GlobalStatement {}
class TypeDeclaration( val typespec:TypeSpecification ) extends GlobalStatement {}

class TypeIsDeclaration( typespec:TypeSpecification ) extends TypeDeclaration( typespec ) {}
class TypeExtendsDeclaration( typespec:TypeSpecification ) extends TypeDeclaration( typespec ) {}

class TypeSpecification() {}
case class PlainType( val indentifier:Identifier ) extends TypeSpecification {}
case class LiteralType() extends TypeSpecification {}
case class NumberType() extends TypeSpecification {}
case class ArrayType( val basetype:TypeSpecification ) extends TypeSpecification {}

object Import extends Function1[PackageSpecification,Import] {
   def apply( packagespec:PackageSpecification ):Import = new Import( packagespec )
   def unapply( i:Import ):Option[PackageSpecification] = Some(i.packagespec)
}
class Import( packagespec:PackageSpecification ) extends ImportStatement( packagespec ) {}

object ImportViral extends Function1[PackageSpecification,ImportViral] {
   def apply( packagespec:PackageSpecification ) = new ImportViral( packagespec )
   def unapply( i:ImportViral ):Option[PackageSpecification] = Some(i.packagespec)
}
class ImportViral( packagespec:PackageSpecification ) extends ImportStatement( packagespec ) {}

case class PackageSpecification( val identifiers:Seq[Identifier] ) {}

case class Parameter( val direction:Direction, val typespec:TypeSpecification, val identifier:Identifier ) {}

case class Identifier( val name:String ) extends SimpleExpression {}
case class Direction( val value:String ) {} //TODO:Should this have subclasses for directions?

case class Scope( val statements:Seq[Statement] ) extends Statement {}

case class ExpressionStatement( val body:ExpressionGroup ) extends Statement {}

case class ExpressionGroup( val expressions:Seq[Expression] ) extends SimpleExpression {}

case class Expression( val simple:SimpleExpression, val next:Option[ChainExpression] ) {}
sealed abstract class SimpleExpression {}
sealed abstract class ChainExpression {}
case class MemberDereference( val member:Identifier, val next:Option[ChainExpression] ) extends ChainExpression {}
case class ArrayExpression( val index:ExpressionGroup, val next:Option[ChainExpression] ) extends ChainExpression {}
//case class ArrayExpression( val base:Expression, val index:Expression ) extends Expression {}
//case class MemberDereference( val base:Expression, val member:Identifier ) extends Expression {}

case class Instantiation( val identifier:Identifier, instancetype:TypeSpecification ) extends Statement {}

case class ForLoop( val init:Expression, val conditional:Expression, val post:Expression, val body:Statement ) extends Statement {}
case class WhileLoop( val conditional:Expression, val body:Statement ) extends Statement {}
case class IfElse( val conditional:Expression, val body_true:Statement, val body_false:Statement ) extends Statement {}

