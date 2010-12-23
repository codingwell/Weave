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
