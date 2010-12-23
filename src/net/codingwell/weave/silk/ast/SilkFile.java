/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
 */

package net.codingwell.weave.silk.ast;

import java.util.Stack;

public class SilkFile
{
	Stack<SilkUsing> usings = new Stack<SilkUsing>();

	public SilkFile addUsing(SilkUsing using)
	{
		usings.push(using);
		return this;
	}
}
