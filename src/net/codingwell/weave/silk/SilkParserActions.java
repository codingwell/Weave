package net.codingwell.weave.silk;

import java.io.IOException;

import net.codingwell.parboiled.FileIncludableInputBuffer;
import net.codingwell.weave.silk.ast.SilkFile;
import net.codingwell.weave.silk.ast.SilkUsing;

import org.parboiled.Context;
import org.parboiled.ContextAware;

public class SilkParserActions implements ContextAware<SilkFile>
{
	public FileIncludableInputBuffer buffer = null;
	protected FileIncludableInputBuffer getBuffer()
	{
		//return (FileIncludableInputBuffer)getContext().getInputBuffer(); // THis doesn't work, its a mutableinputbuffer
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
		try
		{
			getBuffer().include(getContext().getCurrentIndex(), path, replace);
		}
		catch (IOException e)
		{
			//TODO: Better error handling
			return false;
		}
		return true;
	}
	
	private Context<SilkFile> context;
}
