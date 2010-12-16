/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
*/

package net.codingwell.weave;

import java.util.Scanner;

import org.parboiled.Parboiled;
import org.parboiled.common.StringUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import net.codingwell.weave.silk.SilkParser;

public class WeaveCL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SilkParser parser = Parboiled.createParser(SilkParser.class);

        while (true) {
            System.out.print("Enter an expression (single RETURN to exit)!\n");
            String input = new Scanner(System.in).nextLine();
            if (StringUtils.isEmpty(input)) break;

            ParsingResult<SilkParser> result = ReportingParseRunner.run(parser.File(), input);

            if (result.hasErrors()) {
                System.out.println("\nParse Errors:\n" + ErrorUtils.printParseErrors(result));
            }
        }
	}

}
