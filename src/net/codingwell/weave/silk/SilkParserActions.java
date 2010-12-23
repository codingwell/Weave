package net.codingwell.weave.silk;

import net.codingwell.weave.silk.ast.SilkFile;
import net.codingwell.weave.silk.ast.SilkUsing;

import org.parboiled.Context;
import org.parboiled.ContextAware;

public class SilkParserActions implements ContextAware<SilkFile>
{
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

	private Context<SilkFile> context;
}
