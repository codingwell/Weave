/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
 */

package net.codingwell.weave.silk;

import net.codingwell.weave.silk.ast.SilkFile;
import net.codingwell.weave.silk.ast.SilkUsing;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.Label;
import org.parboiled.support.*;

public class SilkParser extends BaseParser<SilkFile>
{
	//Todo: improve this
	public SilkParserActions act = new SilkParserActions();

	public Rule File()
	{
		Var<SilkFile> file = new Var<SilkFile>(new SilkFile());
		return Sequence( ZeroOrMore(GlobalStmt(file)), EOI, push(file.get()) );
	}
	
	public Rule GlobalStmt(Var<SilkFile> file)
	{
		return FirstOf(
			Using(file),
			Include(),
			WS()
		);
	}
	
	public Rule Include()
	{
		Var<String> path = new Var<String>();
		return Sequence(
			Sequence("include", WS(), '"', StringDat(), path.set(matchOrDefault("")) , '"', ';' ),
			act.Include( path.get(), matchOrDefault("").length() )
		);
	}
	
	public Rule StringDat()
	{
		return 
                ZeroOrMore(
                        //FirstOf(
                                //Escape(),
                                Sequence(TestNot(AnyOf("\r\n\"\\")), ANY)
                        //)
                ).suppressSubnodes()
        ;
	}

	public boolean Testme(SilkUsing using, boolean set, int num)
	{
		System.out.print(using == null);
		System.out.print(" ");
		System.out.print(set);
		System.out.print(" ");
		System.out.println(num);
		return true;
	}

	public Rule Using(Var<SilkFile> file)
	{
		Var<SilkUsing> using = new Var<SilkUsing>(new SilkUsing());
		return Sequence(act.Using_Pre(using.get()),
				Sequence("using", WS(), PackageSpec(using), SEMI()),
				act.Using_Post(file.get(), using.get()));
	}

	@Label("';'")
	Rule SEMI()
	{
		return Sequence(';', Optional(WS()));
	}

	Rule ID()
	{
		return OneOrMore(Sequence(
				TestNot(FirstOf(WhiteSpaceChar(), AnyOf(".,{}[];"))), ANY));
	}

	Rule Number()
	{
		return Sequence(
		// we use another Sequence in the "Number" Sequence so we can easily
		// access the input text matched
		// by the three enclosed rules with "lastText()"
				Sequence(OneOrMore(Digit()), Optional('.', OneOrMore(Digit()))),

				// the match() call returns the matched input text of the
				// immediately preceding rule
				// the action uses a default string in case it is run during
				// error recovery (resynchronization)
				EMPTY// push(new
						// CalcNode(Double.parseDouble(matchOrDefault("0")))),
		);
	}

	Rule Digit()
	{
		return CharRange('0', '9');
	}

	protected Rule PackageSpec(Var<SilkUsing> spec)
	{
		return Sequence(
				ID(),
				act.PackageSpec_Push(spec.get(), matchOrDefault("")),
				ZeroOrMore(Sequence('.', ID(),
						act.PackageSpec_Push(spec.get(), matchOrDefault("")))));
	}

	@Label("Whitespace")
	public Rule WS()
	{
		return OneOrMore(WhiteSpaceChar());
	}

	/**
	 * @brief Determines if the current character is a Unicode whitespace
	 *        character.
	 * 
	 *        This function does not support supplementary characters (Multibyte
	 *        UTF-16)
	 * 
	 * @return Rule
	 */
	@Label("Single Whitespace")
	Rule WhiteSpaceChar()
	{
		return AnyOf(" \n\r\t\f");
	}
}
