/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zenscript.javabytecode;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.openzen.zencode.shared.SourceFile;
import org.openzen.zenscript.codemodel.HighLevelDefinition;
import org.openzen.zenscript.codemodel.ScriptBlock;
import org.openzen.zenscript.codemodel.statement.Statement;
import org.openzen.zenscript.compiler.SemanticModule;
import org.openzen.zenscript.compiler.ZenCodeCompiler;
import org.openzen.zenscript.javabytecode.compiler.JavaClassWriter;
import org.openzen.zenscript.javabytecode.compiler.JavaScriptFile;
import org.openzen.zenscript.javabytecode.compiler.JavaStatementVisitor;
import org.openzen.zenscript.javabytecode.compiler.JavaWriter;
import org.openzen.zenscript.javabytecode.compiler.definitions.JavaDefinitionVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hoofdgebruiker
 */
public class JavaCompiler implements ZenCodeCompiler {
	private final JavaModule target;
	private final Map<String, JavaScriptFile> scriptBlocks = new HashMap<>();
	private final JavaClassWriter scriptsClassWriter;
	private int generatedScriptBlockCounter = 0;
	private boolean finished = false;
	private JavaModule compiled = null;

	public JavaCompiler() {
		this(false);
	}

	public JavaCompiler(boolean debug) {
		target = new JavaModule();

		scriptsClassWriter = new JavaClassWriter(ClassWriter.COMPUTE_FRAMES);
		scriptsClassWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Scripts", null, "java/lang/Object", null);
	}

	@Override
	public void addDefinition(HighLevelDefinition definition, SemanticModule module) {
		String className = getClassName(definition.position.filename);
		JavaScriptFile scriptFile = getScriptFile(className);
		target.register(definition.name, definition.accept(new JavaDefinitionVisitor(scriptFile.classWriter)));
	}

	@Override
	public void addScriptBlock(ScriptBlock script) {
		final SourceFile sourceFile = script.getTag(SourceFile.class);
		final String className = getClassName(sourceFile == null ? null : sourceFile.getFilename());
		JavaScriptFile scriptFile = getScriptFile(className);

		String methodName = scriptFile.scriptMethods.isEmpty() ? "run" : "run" + scriptFile.scriptMethods.size();

		// convert scripts into methods (add them to a Scripts class?)
		// (TODO: can we break very long scripts into smaller methods? for the extreme scripts)
		final JavaClassWriter visitor = scriptFile.classWriter;
		JavaMethodInfo method = new JavaMethodInfo(new JavaClassInfo(className), methodName, "()V", Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
		scriptFile.scriptMethods.add(method);

		final JavaStatementVisitor statementVisitor = new JavaStatementVisitor(new JavaWriter(visitor, method, null, null, null));
		statementVisitor.start();
		for (Statement statement : script.statements) {
			statement.accept(statementVisitor);
		}
		target.register("Scripts", scriptsClassWriter.toByteArray());
		statementVisitor.end();
	}

	private String getClassName(String filename) {
		if (filename == null) {
			return "generatedBlock" + (generatedScriptBlockCounter++);
		} else {
			// TODO: remove special characters
			System.out.println("Writing script: " + filename);
			return filename.substring(0, filename.lastIndexOf('.')).replace("/", "_");
		}
	}

	private JavaScriptFile getScriptFile(String className) {
		if (!scriptBlocks.containsKey(className)) {
			JavaClassWriter scriptFileWriter = new JavaClassWriter(ClassWriter.COMPUTE_FRAMES);
			scriptFileWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);
			scriptBlocks.put(className, new JavaScriptFile(scriptFileWriter));
		}

		return scriptBlocks.get(className);
	}

	@Override
	public void finish() {
		finishAndGetModule();
	}

	@Override
	public void run() {
		if (compiled == null)
			throw new IllegalStateException("Not yet built!");


	}

	public JavaModule finishAndGetModule() {
		if (finished)
			throw new IllegalStateException("Already finished!");
		finished = true;

		JavaMethodInfo runMethod = new JavaMethodInfo(new JavaClassInfo("Scripts"), "run", "()V", Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
		final JavaWriter runWriter = new JavaWriter(scriptsClassWriter, runMethod, null, null, null);
		runWriter.start();
		for (Map.Entry<String, JavaScriptFile> entry : scriptBlocks.entrySet()) {
			for (JavaMethodInfo method : entry.getValue().scriptMethods)
				runWriter.invokeStatic(method);

			entry.getValue().classWriter.visitEnd();
			target.register(entry.getKey(), entry.getValue().classWriter.toByteArray());
		}
		runWriter.ret();
		runWriter.end();

		target.register("Scripts", scriptsClassWriter.toByteArray());

		return target;
	}
}
