package net.codingwell.parboiled;

import java.util.List;

import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.StringUtils;
import org.parboiled.errors.DefaultInvalidInputErrorFormatter;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.support.Position;

import static org.parboiled.common.Preconditions.*;

/**
 * 
 * Thrown together a way to print errors from IncludableInputBuffer
 * This is a temporary hack as eventually errors will be translated into a Class that can be passes around
 * 
 * @author tsuckow
 *
 */
public class ErrorUtils
{
    /**
     * Pretty prints the given parse errors showing their location in the given input buffer.
     *
     * @param errors      the parse errors
     * @return the pretty print text
     */
    public static String printParseErrors(List<ParseError> errors, IncludableInputBuffer<String> buffer)
    {
        checkArgNotNull(errors, "errors");
        StringBuilder sb = new StringBuilder();
        for (ParseError error : errors) 
        {
            if (sb.length() > 0) sb.append("---\n");
            sb.append(printParseError(error, buffer));
        }
        return sb.toString();
    }
    
    public static String printParseError(ParseError error, IncludableInputBuffer<String> buffer)
    {
        checkArgNotNull(error, "error");
        DefaultInvalidInputErrorFormatter formatter = new DefaultInvalidInputErrorFormatter();
        String message = error.getErrorMessage() != null ? error.getErrorMessage() :
                error instanceof InvalidInputError ?
                        formatter.format((InvalidInputError) error) : error.getClass().getSimpleName();
        InputBuffer ebuffer = error.getInputBuffer();
        return printErrorMessage("%s %s(line %s, col %s):", message,
        		ebuffer.getOriginalIndex(error.getStartIndex()),
        		ebuffer.getOriginalIndex(error.getEndIndex()),
        		buffer);
    }
    
    public static String printErrorMessage(String format, String errorMessage, int startIndex, int endIndex, IncludableInputBuffer<String> buffer)
    {
		checkArgNotNull(buffer, "inputBuffer");
		checkArgument(startIndex <= endIndex);
		Position uncorrectedpos = buffer.getPosition(startIndex);
		Position pos = buffer.getLocalPosition(startIndex);
		StringBuilder sb = new StringBuilder(String.format(format, errorMessage, buffer.handleAt(startIndex), pos.line, pos.column));
		sb.append('\n');
		
		String line = buffer.extractLine(uncorrectedpos.line);
		sb.append(line);
		sb.append('\n');
		
		int charCount = Math.max(Math.min(endIndex - startIndex, StringUtils.length(line) - pos.column + 2), 1);
		for (int i = 0; i < pos.column - 1; i++) sb.append(' ');
		for (int i = 0; i < charCount; i++) sb.append('^');
		sb.append("\n");
		
		return sb.toString();
	}
}
