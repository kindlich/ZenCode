/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.parser.definitions;

import java.util.List;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zenscript.codemodel.HighLevelDefinition;
import org.openzen.zenscript.codemodel.definition.AliasDefinition;
import org.openzen.zenscript.codemodel.definition.ZSPackage;
import org.openzen.zenscript.codemodel.generic.TypeParameter;
import org.openzen.zenscript.lexer.ZSTokenParser;
import org.openzen.zenscript.lexer.ZSTokenType;
import org.openzen.zenscript.codemodel.scope.BaseScope;
import org.openzen.zenscript.codemodel.scope.DefinitionScope;
import org.openzen.zenscript.parser.ParsedAnnotation;
import org.openzen.zenscript.parser.ParsedDefinition;
import org.openzen.zenscript.parser.type.IParsedType;

/**
 *
 * @author Hoofdgebruiker
 */
public class ParsedAlias extends ParsedDefinition {
	public static ParsedAlias parseAlias(ZSPackage pkg, CodePosition position, int modifiers, ParsedAnnotation[] annotations, ZSTokenParser tokens, HighLevelDefinition outerDefinition) {
		String name = tokens.required(ZSTokenType.T_IDENTIFIER, "identifier expected").content;
		List<ParsedGenericParameter> parameters = ParsedGenericParameter.parseAll(tokens);
		IParsedType type = IParsedType.parse(tokens);
		return new ParsedAlias(pkg, position, modifiers, annotations, name, parameters, type, outerDefinition);
	}
	
	private final String name;
	private final List<ParsedGenericParameter> parameters;
	private final IParsedType type;
	
	private final AliasDefinition compiled;
	
	public ParsedAlias(ZSPackage pkg, CodePosition position, int modifiers, ParsedAnnotation[] annotations, String name, List<ParsedGenericParameter> parameters, IParsedType type, HighLevelDefinition outerDefinition) {
		super(position, modifiers, annotations);
		
		this.name = name;
		this.parameters = parameters;
		this.type = type;
		
		compiled = new AliasDefinition(position, pkg, name, modifiers, outerDefinition);
	}
	
	@Override
	public HighLevelDefinition getCompiled() {
		return compiled;
	}

	@Override
	public void compileMembers(BaseScope scope) {
		if (parameters.size() > 0) {
			TypeParameter[] typeParameters = new TypeParameter[parameters.size()];
			for (int i = 0; i < parameters.size(); i++) {
				typeParameters[i] = parameters.get(i).compiled;
			}
			compiled.setTypeParameters(typeParameters);
		}
		
		DefinitionScope innerScope = new DefinitionScope(scope, compiled);
		for (int i = 0; i < compiled.genericParameters.length; i++) {
			TypeParameter output = compiled.genericParameters[i];
			ParsedGenericParameter input = this.parameters.get(i);
			for (ParsedGenericBound bound : input.bounds) {
				output.addBound(bound.compile(innerScope));
			}
		}
		
		compiled.setType(type.compile(innerScope));
	}

	@Override
	public void compileCode(BaseScope scope) {
		// nothing to do
	}

	@Override
	public void linkInnerTypes() {
		// nothing to do
	}
}
