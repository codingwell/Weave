// Copyright (c) 2012 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.verilog.ast

case class File ( statements:Seq[GlobalStatement] )

class GlobalStatement ( )

case class Module ( name:String, parameters:Seq[Parameter], statements:Seq[ModuleStatement] )

case class Parameter( direction:String, vartype:String, varname:String )

class ModuleStatement ()

case class DeclarationStatement( vartype:String, varname:String )

case class AssignmentStatement( lhs:String )
