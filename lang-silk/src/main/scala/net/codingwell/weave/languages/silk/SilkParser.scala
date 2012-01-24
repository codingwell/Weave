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

  sealed abstract class AstNode
  case class ASTFile(members: List[ASTGlobalStatement])
  sealed abstract class ASTGlobalStatement
  case object ASTUsing extends ASTGlobalStatement
  case object ASTInclude extends ASTGlobalStatement
  case object PlaceHolder extends AstNode
  
  def File = rule { OWhiteSpace ~ zeroOrMore(ZGlobalStatement) ~~> ast.File ~ EOI }
  
  def ZGlobalStatement = { ZImport | ZImportViral }
  def ZImport = rule { "import" ~ WhiteSpace ~ ZPackageSpecification ~~> ast.Import }
  def ZImportViral = rule { "importviral" ~ WhiteSpace ~ ZPackageSpecification ~~> ast.ImportViral }
  def ZPackageSpecification = rule { oneOrMore( ZIdentifier, "." ) ~~> ast.PackageSpecification }
  def ZIdentifier = rule { ID ~> ast.Identifier }


  def GlobalStatement = rule { Using | TempInclude }
  
  def TempInclude = rule { Include ~ push(ASTInclude) }
  
  def DoInclude(s:ast.QuotedString, r:IndexRange, context:Context[Any]):Unit =
  {
    println( "DOINCLUDE: " + s.text + "  " + r.toString() )
    buffer.include( r.end, Preprocessor.StripComments( FileUtils.readAllText(s.text) ), s.text, r.end-r.start);
    println(s.text)
  }
  
  def Include = rule { IncludeInternal ~>> withContext( OriginalIndexRange ) ~~% withContext(DoInclude _) }
  def IncludeInternal = rule { "include" ~ WhiteSpace ~ SilkString ~ SEMI }
  
  def Using = rule { "using" ~ WhiteSpace ~ PackageSpec ~ SEMI ~ push(ASTUsing) }
  
  def SEMI = rule("';'") { ";" ~ OWhiteSpace }
  
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
  def WhiteSpace: Rule0 = rule { oneOrMore(anyOf(" \n\r\t\f")) }
  def OWhiteSpace: Rule0 = rule("WhiteSpace") { optional( WhiteSpace ) }
}
