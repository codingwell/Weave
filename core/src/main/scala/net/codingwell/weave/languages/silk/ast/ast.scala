// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk.ast

case class File( val members:Seq[GlobalStatement])

sealed abstract class Statement

class GlobalStatement() extends Statement {}

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
}
class Import( packagespec:PackageSpecification ) extends ImportStatement( packagespec ) {}

object ImportViral extends Function1[PackageSpecification,ImportViral] {
   def apply( packagespec:PackageSpecification ):ImportViral = new ImportViral( packagespec )
}
class ImportViral( packagespec:PackageSpecification ) extends ImportStatement( packagespec ) {}

case class PackageSpecification( val identifiers:Seq[Identifier] ) {}

case class Parameter( val identifier:Identifier, val direction:Direction, val typespec:TypeSpecification ) {}

case class Identifier( val name:String ) extends Expression {}
case class Direction() {}

case class Scope( val statement:Seq[Statement] ) extends Statement {}

class Expression() extends Statement {}
case class Instantiation( val identifier:Identifier, instancetype:TypeSpecification ) extends Statement {}

case class ForLoop( val init:Expression, val conditional:Expression, val post:Expression, val body:Statement ) extends Statement {}
case class WhileLoop( val conditional:Expression, val body:Statement ) extends Statement {}
case class IfElse( val conditional:Expression, val body_true:Statement, val body_false:Statement ) extends Statement {}

