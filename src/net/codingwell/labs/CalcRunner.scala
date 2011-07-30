package net.codingwell.labs
import org.parboiled.scala.parserunners.ReportingParseRunner
import net.codingwell.parboiled.IncludableInputBuffer
import org.parboiled.scala.Input
import net.codingwell.weave.silk.Preprocessor
import org.parboiled.common.FileUtils

class CalcRunner {
	def Run =
	{
		val buffer = new IncludableInputBuffer[String]
		buffer.include(0, Preprocessor.StripComments( FileUtils.readAllText("scala.silk") ), "scala.silk", 0);//Load up first file
		val parser = new net.codingwell.weave.silk.ScalaSilkParser(buffer) { override val buildParseTree = true }
		val runner = ReportingParseRunner(parser.File)
		val result = runner.run( new Input( null, (A:Array[Char]) => buffer ) ) //This is kinda hackish, ideally we would pass filename to buffer here
		val parseTreePrintOut = org.parboiled.support.ParseTreeUtils.printNodeTree(result)
		//println(parseTreePrintOut)
		val file = result.result.get
		println( file.members.size )
		buffer.debugOutput()
	}
}