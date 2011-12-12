package net.codingwell.labs.logging

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Color._

class ConsoleLogger extends Logger
{
   def bla()
   {
      println( Ansi.ansi().a("BLA").fg(BLUE).a("Blue").reset() )
      println( Ansi.ansi().render("@|bold, white TEst|@").reset() )
      println( Ansi.ansi().render("@|white TEst|@").reset() )
      println( Ansi.ansi().render("@|faint, white TEst|@").reset() )
      println( Ansi.ansi().render("@|bold MyFile.silk|@@|green :124|@: ").render("@|bold,red Error: |@Unexpected ';', remove this token").reset() )
      println( Ansi.ansi().render("@|bold MyFile.silk|@@|green :125|@: ").render("@|yellow Warning: |@Unreachable statement").reset() )
   }
}
