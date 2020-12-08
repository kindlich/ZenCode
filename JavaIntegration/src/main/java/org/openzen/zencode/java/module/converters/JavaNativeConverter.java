package org.openzen.zencode.java.module.converters;

public class JavaNativeConverter {
	public final JavaNativeTypeConverter typeConverter;
	public final JavaNativeHeaderConverter headerConverter;
	public final JavaNativeMemberConverter memberConverter;

	public final JavaNativeClassConverter classConverter;
	public final JavaNativeGlobalConverter globalConverter;
	public final JavaNativeExpansionConverter expansionConverter;

	public JavaNativeConverter(JavaNativeTypeConverter typeConverter, JavaNativeHeaderConverter headerConverter, JavaNativeMemberConverter memberConverter, JavaNativeClassConverter classConverter, JavaNativeGlobalConverter globalConverter, JavaNativeExpansionConverter expansionConverter) {
		this.typeConverter = typeConverter;
		this.headerConverter = headerConverter;
		this.memberConverter = memberConverter;
		this.classConverter = classConverter;
		this.globalConverter = globalConverter;
		this.expansionConverter = expansionConverter;
	}
}
