/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
 */
package net.codingwell.weave.silk;

import net.codingwell.parboiled.IncludableInputBuffer;
import net.codingwell.weave.silk.ast.SilkFile;
import net.codingwell.weave.silk.ast.SilkUsing;

import org.parboiled.Context;
import org.parboiled.ContextAware;
import org.parboiled.common.FileUtils;

/**
 * 
 * @author tsuckow
 * TODO: FileUtils in Parboiled doesn't throw exceptions, we want the error messages. 
 */
public class SilkParserActions implements ContextAware<SilkFile>
{
	public IncludableInputBuffer<String> buffer = null;
	private Context<SilkFile> context;
	
	protected IncludableInputBuffer<String> getBuffer()
	{
		return buffer;
	}
	
	boolean Using_Pre(SilkUsing using)
	{
		using.posStart = getContext().getInputBuffer().getPosition(
				getContext().getCurrentIndex());
		return true;
	}

	boolean Using_Post(SilkFile file, SilkUsing using)
	{
		using.posEnd = getContext().getInputBuffer().getPosition(
				getContext().getCurrentIndex());
		file.addUsing(using);
		return true;
	}

	boolean PackageSpec_Push(SilkUsing using, String name)
	{
		using.spec.push(name);
		return true;
	}

	private Context<SilkFile> getContext()
	{
		return this.context;
	}

	@Override
	public void setContext(Context<SilkFile> context)
	{
		this.context = context;
	}

	boolean Include( String path, int replace )
	{
		getBuffer().include(getContext().getCurrentIndex(), Preprocessor.StripComments( FileUtils.readAllText(path) ), path, replace);
		
		return true;
	}
}
