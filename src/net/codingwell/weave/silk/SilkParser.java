/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
*/

package net.codingwell.weave.silk;

import net.codingwell.weave.silk.ast.SilkFile;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.*;

public class SilkParser extends BaseParser<SilkFile>
{
	public Rule File() 
	{
		Var<SilkFile> file = new Var<SilkFile>();
        StringVar text = new StringVar("");
        StringVar temp = new StringVar("");
        return Sequence(
            OneOrMore(
                " "
            ),
            push( file.get() )
        );
	}
}
