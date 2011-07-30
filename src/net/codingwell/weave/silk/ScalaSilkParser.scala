package net.codingwell.weave.silk

import org.parboiled.scala._
import java.lang.String
import org.parboiled.errors.{ErrorUtils, ParsingException}
import net.codingwell.parboiled.IncludableInputBuffer
import org.parboiled.support.IndexRange
import org.parboiled.Context
import org.parboiled.common.FileUtils

class ScalaSilkParser(val buffer: IncludableInputBuffer[String]) extends Parser
{

  sealed abstract class AstNode
  case class ASTFile(members: List[ASTGlobalStatement])
  sealed abstract class ASTGlobalStatement
  case object ASTUsing extends ASTGlobalStatement
  case object ASTInclude extends ASTGlobalStatement
  case class ASTString(text: String)
  case class ASTIndexRange(range: IndexRange)
  case object PlaceHolder extends AstNode
  
  def File = rule { OWhiteSpace ~ zeroOrMore(GlobalStatement) ~~> ASTFile ~ EOI }
  
  def GlobalStatement = rule { Using | TempInclude }
  
  def TempInclude = rule { Include ~ push(ASTInclude) }
  
  def DoInclude(s:ASTString, r:ASTIndexRange):Unit =
  {
    val len = buffer.length()
    println( "DOINCLUDE: " + s.text + "  " + r.range.toString() + " " + buffer.length() )
    buffer.include( r.range.end, Preprocessor.StripComments( FileUtils.readAllText(s.text) ), s.text, r.range.length());
    println(s.text) 
    if( len != buffer.length() )
    {
       println( "DOINCLUDE CHANGED: " + buffer.length() )
    }
  }
  
  def Include = rule { IncludeInternal ~>> ASTIndexRange ~~% DoInclude _ }
  def IncludeInternal = rule { "include" ~ WhiteSpace ~ SilkString ~ SEMI }
  
  def Using = rule { "using" ~ WhiteSpace ~ PackageSpec ~ SEMI ~ push(ASTUsing) }
  
  def SEMI = rule { ";" ~ OWhiteSpace }
  
  def PackageSpec = rule { oneOrMore( ID, "." ) }
  def ID = rule { oneOrMore( !anyOf(".,{}[]; \n\r\t\f") ~ ANY ) }//TODO this must be formalized with whitespace rule
  
  //TODO: AST
  def Number = rule { Decimal | Integer } //Order matters
  def Decimal = rule { oneOrMore( Digit ) ~  "." ~ oneOrMore( Digit ) }
  def Integer = rule { oneOrMore( Digit ) }
  def Digit = rule { "0"-"9" }
  
  def SilkString = rule { "\"" ~ zeroOrMore( !anyOf("\r\n\"\\") ~ ANY ) ~> ASTString ~ "\"" ~ WhiteSpace }//OWhiteSpace
  def WhiteSpace: Rule0 = rule { oneOrMore(anyOf(" \n\r\t\f")) }
  def OWhiteSpace: Rule0 = rule { optional( WhiteSpace ) }
}