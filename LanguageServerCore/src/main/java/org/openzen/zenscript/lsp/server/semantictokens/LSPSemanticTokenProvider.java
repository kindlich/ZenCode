package org.openzen.zenscript.lsp.server.semantictokens;

import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.openzen.zenscript.lexer.ZSTokenType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class LSPSemanticTokenProvider {

	public CompletableFuture<SemanticTokens> tokensFull(SemanticTokensParams params) {
		return CompletableFuture.supplyAsync(() -> new SemanticTokens(Arrays.asList(1, 2, 1, 1, 1, 1, 1, 1)));
	}

	public SemanticTokensWithRegistrationOptions getOptions() {

		final ZSTokenType[] values = ZSTokenType.values();
		final List<String> tokenTypes = getTokenTypes(values);
		final List<String> tokenModifiers = getTokenModifiers(values);
		final SemanticTokensLegend legend = new SemanticTokensLegend(tokenTypes, tokenModifiers);
		return new SemanticTokensWithRegistrationOptions(legend, true);
	}

	private List<String> getTokenTypes(ZSTokenType[] types) {
		return Arrays.stream(types).map(Enum::name).collect(Collectors.toList());
	}

	private List<String> getTokenModifiers(ZSTokenType[] types) {
		return Arrays.stream(types).map(Enum::name).collect(Collectors.toList());
	}
}
