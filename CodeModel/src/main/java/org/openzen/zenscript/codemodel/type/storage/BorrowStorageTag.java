/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.codemodel.type.storage;

/**
 *
 * @author Hoofdgebruiker
 */
public class BorrowStorageTag implements StorageTag {
	public static final BorrowStorageTag INVOCATION = new BorrowStorageTag();
	
	private BorrowStorageTag() {} // TODO: scoped borrow

	@Override
	public StorageType getType() {
		return BorrowStorageType.INSTANCE;
	}
}
