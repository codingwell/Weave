// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import org.parboiled.scala._
import net.codingwell.parboiled.Helpers._
import java.lang.String
import org.parboiled.errors.{ErrorUtils, ParsingException}
import net.codingwell.parboiled.IncludableInputBuffer
import org.parboiled.support.IndexRange
import org.parboiled.Context
import org.parboiled.common.FileUtils

class SilkParser(val buffer: IncludableInputBuffer[String]) extends Parser
{
  def File = { OWhiteSpace ~ zeroOrMore(GlobalStatement) ~~> ast.File ~ EOI }

  def GlobalStatement = rule { Import | ImportViral | Module }
  def Import = rule { "import" ~ WhiteSpace ~ PackageSpecification ~~> ast.Import ~ OWhiteSpace ~ SEMI }
  def ImportViral = rule { "importviral" ~ WhiteSpace ~ PackageSpecification ~~> ast.ImportViral ~ OWhiteSpace ~ SEMI }
  def PackageSpecification = rule { oneOrMore( Identifier, "." ) ~~> ast.PackageSpecification }

  def Identifier = rule { ID ~> ast.Identifier }

  def Module = rule { "module" ~ WhiteSpace ~ Identifier ~ OWhiteSpace ~ zeroOrMore(ModuleParameter) ~ Scope ~~> ast.Module }
  def ModuleParameter = rule { Direction ~ WhiteSpace ~ TypeSpecification ~ WhiteSpace ~ Identifier ~ OWhiteSpace ~ SEMI ~~> ast.Parameter }

  def Direction = rule { ("in" | "out") ~> ast.Direction }
  def TypeSpecification = rule { PlainType }
  def PlainType = rule { Identifier ~~> ast.PlainType }
  def LiteralType = rule { WhiteSpace }//TODO
  def NumberType = rule { WhiteSpace }//TODO
  def ArrayType = rule { WhiteSpace }//TODO

  def Scope = rule { "{" ~ OWhiteSpace ~ zeroOrMore(Statement) ~ "}" ~~> ast.Scope ~ OWhiteSpace }

  def Statement:Rule1[ast.Statement] = rule { GlobalStatement | ExpressionStatement | Instantiation }

  def ExpressionStatement = rule { ExpressionGroup ~ SEMI ~~> ast.ExpressionStatement }

  def ExpressionGroup = rule { oneOrMore( Expression, OWhiteSpace ) ~~> ast.ExpressionGroup ~ OWhiteSpace }

  def Expression:Rule1[ast.Expression] = rule { SimpleExpression ~ ChainExpression ~~> ast.Expression }
  def SimpleExpression = rule { Identifier | ParenExpression }
  def ChainExpression:Rule1[Option[ast.ChainExpression]] = rule { optional( ArrayExpression | MemberDereference ) }

  def ParenExpression = rule { "(" ~ OWhiteSpace ~ ExpressionGroup ~ ")" }

  def ArrayExpression = rule { "[" ~ OWhiteSpace ~ ExpressionGroup ~ "]" ~ OWhiteSpace ~ ChainExpression ~~> ast.ArrayExpression }
  def MemberDereference = rule { "." ~ Identifier ~ ChainExpression ~~> ast.MemberDereference }

  //def ArrayExpression = rule { Expression ~ "[" ~ OWhiteSpace ~ Expression ~ "]" ~~> ast.ArrayExpression }
  //TODO Inline Array Expression. AKA. {a,b,c,d}
  //def InlineArrayExpression = rule { }
  //def MemberDereference = rule { Expression ~ "." ~ Identifier ~~> ast.MemberDereference }
  //def ParenExpression = rule { "(" ~ OWhiteSpace ~ ExpressionGroup ~ ")" }

  def Instantiation = rule { "var" ~ WhiteSpace ~ Identifier ~ WhiteSpace ~ TypeSpecification ~ OWhiteSpace ~ SEMI ~~> ast.Instantiation }

  def SEMI = rule("';'") { ";" ~ OWhiteSpace }
  
  @Deprecated
  def PackageSpec = rule { oneOrMore( ID, "." ) }
  def ID = rule { oneOrMore( !anyOf(".,{}[]; \n\r\t\f") ~ ANY ) }//TODO this must be formalized with whitespace rule
  
  //TODO: AST
  def Number = rule { Decimal | Integer } //Order matters
  def Decimal = rule { oneOrMore( Digit ) ~  "." ~ oneOrMore( Digit ) }
  def Integer = rule { oneOrMore( Digit ) }
  def Digit = rule { "0"-"9" }
  
  //ID Concept:
  //Use scala's
  
  def SilkString = rule("String") { "\"" ~ zeroOrMore( !anyOf("\r\n\"\\") ~ ANY ) ~> ((s:String) => s) ~>> withContext( OriginalIndexRange ) ~~> ast.QuotedString ~ "\"" ~ OWhiteSpace }
  def WhiteSpace: Rule0 = rule("Whitespace") { oneOrMore(anyOf(" \n\r\t\f")) }
  def OWhiteSpace: Rule0 = rule("Whitespace") { optional( WhiteSpace ) }
}
