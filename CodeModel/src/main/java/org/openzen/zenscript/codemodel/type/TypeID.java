/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.codemodel.type;

import java.util.List;
import java.util.Map;
import org.openzen.zenscript.codemodel.GenericMapper;
import org.openzen.zenscript.codemodel.HighLevelDefinition;
import org.openzen.zenscript.codemodel.generic.TypeParameter;
import org.openzen.zenscript.codemodel.type.member.LocalMemberCache;
import org.openzen.zenscript.codemodel.type.storage.StorageTag;
import org.openzen.zenscript.codemodel.type.storage.ValueStorageTag;

/**
 *
 * @author Hoofdgebruiker
 */
public interface TypeID {
	public static final TypeID[] NONE = new TypeID[0];
	
	default TypeID getSuperType(GlobalTypeRegistry registry) {
		return null;
	}
	
	TypeID instanceUnstored(GenericMapper mapper);
	
	TypeID getNormalizedUnstored();
	
	boolean isDestructible();
	
	boolean hasDefaultValue();
	
	boolean hasInferenceBlockingTypeParameters(TypeParameter[] parameters);
	
	// Infers type parameters for this type so it matches with targetType
	// returns false if that isn't possible
	default Map<TypeParameter, TypeID> inferTypeParameters(LocalMemberCache cache, TypeID targetType) {
		return TypeMatcher.match(cache, this, targetType);
	}
	
	void extractTypeParameters(List<TypeParameter> typeParameters);
	
	<R> R accept(TypeVisitor<R> visitor);
	
	<C, R, E extends Exception> R accept(C context, TypeVisitorWithContext<C, R, E> visitor) throws E;
	
	default StoredType stored(StorageTag storage) {
		return new StoredType(this, storage);
	}
	
	default boolean isOptional() {
		return false;
	}
	
	default boolean isConst() {
		return false;
	}
	
	default boolean isImmutable() {
		return false;
	}
	
	default boolean isGeneric() {
		return false;
	}
	
	default TypeID withoutOptional() {
		throw new UnsupportedOperationException("Not an optional type");
	}
	
	default TypeID withoutConst() {
		throw new UnsupportedOperationException("Not a const type");
	}
	
	default TypeID withoutImmutable() {
		throw new UnsupportedOperationException("Not an immutable type");
	}
	
	default boolean isVariant() {
		return false;
	}
	
	default boolean isEnum() {
		return false;
	}
	
	default boolean isDefinition(HighLevelDefinition definition) {
		return false;
	}
	
	default String toString(StorageTag storage) {
		if (storage == ValueStorageTag.INSTANCE)
			return toString();
		
		return toString() + "`" + storage.toString();
	}
}