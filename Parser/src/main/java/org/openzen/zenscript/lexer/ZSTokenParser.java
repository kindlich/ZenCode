/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.lexer;

import java.io.IOException;
import org.openzen.zencode.shared.SourceFile;
import org.openzen.zenscript.codemodel.WhitespaceInfo;
import org.openzen.zenscript.parser.BracketExpressionParser;

/**
 *
 * @author Hoofdgebruiker
 */
public class ZSTokenParser extends LLParserTokenStream<ZSTokenType, ZSToken> {
	private static final CompiledDFA DFA = CompiledDFA.createLexerDFA(ZSTokenType.values(), ZSTokenType.class);
	
	public static TokenParser<ZSToken, ZSTokenType> createRaw(SourceFile file, CharReader reader, int spacesPerTab) {
		return new TokenParser<>(
				file,
				reader,
				DFA,
				ZSTokenType.EOF,
				ZSTokenType.INVALID,
				new ZSTokenFactory(spacesPerTab));
	}
	
	public static ZSTokenParser create(SourceFile file, BracketExpressionParser bracketParser, int spacesPerTab) throws IOException {
		return new ZSTokenParser(createRaw(file, new ReaderCharReader(file.open()), spacesPerTab), bracketParser);
	}
	
	public final BracketExpressionParser bracketParser;
	
	public ZSTokenParser(TokenStream<ZSTokenType, ZSToken> parser, BracketExpressionParser bracketParser) {
		super(parser);
		
		this.bracketParser = bracketParser;
	}
	
	public SourceFile getFile() {
		return getPosition().file;
	}
	
	public WhitespaceInfo collectWhitespaceInfo(String whitespace, boolean skipLineBefore) {
		return WhitespaceInfo.from(whitespace, grabWhitespaceLine(), skipLineBefore);
	}
}
