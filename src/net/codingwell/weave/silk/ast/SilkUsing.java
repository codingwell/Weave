/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
 */
package net.codingwell.weave.silk.ast;

import java.util.Stack;

import org.parboiled.buffers.InputBuffer.Position;

import net.codingwell.weave.ASTAttr;

/**
 * 
 * AST Node representing a using declaration.
 * 
 * @author tsuckow
 * 
 */
public class SilkUsing implements ASTAttr
{
	public Stack<String> spec = new Stack<String>();

	public Position posStart;
	public Position posEnd;

	@Override
	public Position GetStartPosition()
	{
		return posStart;
	}

	@Override
	public Position GetEndPosition()
	{
		return posEnd;
	}
}
