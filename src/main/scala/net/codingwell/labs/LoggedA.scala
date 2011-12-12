package net.codingwell.labs

import net.codingwell.labs.logging._

class LoggedA(implicit protected val logger: Logger) extends Logging
{
	val hi = new LoggedB
	
	def doStuff()
	{
		logger.bla()
	}
}
