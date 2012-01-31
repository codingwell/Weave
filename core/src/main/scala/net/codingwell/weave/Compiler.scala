package net.codingwell.weave

class RTLPlaceholder {}

trait Compiler {
	def compile( file:WeaveFile ):RTLPlaceholder
	
	def supportedLanguages():Set[String]
}