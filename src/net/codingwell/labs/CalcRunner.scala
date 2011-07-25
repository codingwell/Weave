package net.codingwell.labs
import org.parboiled.scala.parserunners.ReportingParseRunner

class CalcRunner {
	def Run(in: String)
	{
		val input = in
		val parser = new SimpleScalaCalc { override val buildParseTree = true }
		val result = ReportingParseRunner(parser.Expression).run(input)
		val parseTreePrintOut = org.parboiled.support.ParseTreeUtils.printNodeTree(result)
		println(parseTreePrintOut)
	}
}