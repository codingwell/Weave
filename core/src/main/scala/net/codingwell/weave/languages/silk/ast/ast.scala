// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk.ast

case class File( val members:Seq[GlobalStatement])

//TODO: Should Decl->Declaration?, Spec->Specification?
//TODO: Classes will need object with apply method

sealed abstract class Statement

class GlobalStatement() extends Statement {}

case class Module( val identifier:Identifier, val parameters:Seq[Parameter] , val scope:Scope ) extends GlobalStatement {}
class ImportStatement( val packagespec:PackageSpec ) extends GlobalStatement {}
class TypeDecl( val typespec:TypeSpec ) extends GlobalStatement {}

class TypeIsDecl( typespec:TypeSpec ) extends TypeDecl( typespec ) {}
class TypeExtendsDecl( typespec:TypeSpec ) extends TypeDecl( typespec ) {}

class TypeSpec() {}
case class PlainType( val indentifier:Identifier ) extends TypeSpec {}
case class LiteralType() extends TypeSpec {}
case class NumberType() extends TypeSpec {}
case class ArrayType( val basetype:TypeSpec ) extends TypeSpec {}

class Import( packagespec:PackageSpec ) extends ImportStatement( packagespec ) {}
class ImportViral( packagespec:PackageSpec ) extends ImportStatement( packagespec ) {}

case class PackageSpec( val identifiers:Seq[Identifier] ) {}

case class Parameter( val identifier:Identifier, val direction:Direction, val typespec:TypeSpec ) {}

case class Identifier() extends Expression {}
case class Direction() {}

case class Scope( val statement:Seq[Statement] ) extends Statement {}

class Expression() extends Statement {}
case class Instantiation( val identifier:Identifier, instancetype:TypeSpec ) extends Statement {}

case class ForLoop( val init:Expression, val conditional:Expression, val post:Expression, val body:Statement ) extends Statement {}
case class WhileLoop( val conditional:Expression, val body:Statement ) extends Statement {}
case class IfElse( val conditional:Expression, val body_true:Statement, val body_false:Statement ) extends Statement {}
