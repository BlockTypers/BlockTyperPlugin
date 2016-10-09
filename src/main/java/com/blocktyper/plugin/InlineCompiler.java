package com.blocktyper.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class InlineCompiler {

	public static void main(BlockTyperPlugin plugin)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String source = ""
				+ " public class DynamicListener { \n"
				+ " public void blockDamage() { \n"
				+ " System.out.println(\"ARGGGG!!\"); \n"
				+ " } \n"
				+ " } \n";

		plugin.debugInfo("Creating temp file for dynamic source");

		Path path = Files.createTempFile("DynamicListener", ".java");
		String fileName = path.getFileName().toString();
		plugin.debugInfo("Loading temp file: " + fileName);
		File sourceFile = path.toFile();

		String className = fileName.substring(0, fileName.indexOf("."));

		plugin.debugInfo("className: " + className);

		source = source.replaceAll("DynamicListener", className);

		plugin.debugInfo("new source : " + source);

		// File sourceFile = new File(root, "test/DynamicListener.java");
		// debugInfo("making temp java directory");
		// File root = sourceFile.getParentFile().getParentFile();
		// root.mkdirs();

		// debugInfo("making temp test directory");
		// sourceFile.getParentFile().mkdirs();

		plugin.debugInfo("Writing source to temp file");
		Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

		/**
		 * Compilation Requirements
		 *********************************************************************************************/
		plugin.debugInfo("Setting classpath");
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

		// This sets up the class path that the compiler will use.
		// I've added the .jar file that contains the DoStuff interface within
		// in it...
		List<String> optionList = new ArrayList<String>();
		optionList.add("-classpath");

		File pluginDataFolder = plugin.getDataFolder();
		if (pluginDataFolder == null) {
			plugin.debugWarning("pluginDataFolder was null");
			return;
		} else {
			plugin.debugInfo("pluginDataFolder: " + pluginDataFolder.getPath());
		}

		
		String magicDoorsJar = pluginDataFolder.getPath() + "/MagicDoors.jar;";
		magicDoorsJar = "/home/spaarkimus/minecraftservers/server1/plugins/MagicDoors/MagicDoors.jar;";
		String classPathAppend = System.getProperty("java.class.path") + ";" 
				+ magicDoorsJar;
		plugin.debugInfo("adding java to classpath: " + classPathAppend);
		optionList.add(classPathAppend);

		plugin.debugInfo("Compiling");
		Iterable<? extends JavaFileObject> compilationUnit = fileManager
				.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				compilationUnit);
		/*********************************************************************************************
		 * Compilation Requirements
		 **/
		if (task.call()) {
			/**
			 * Load and execute
			 *************************************************************************************************/
			System.out.println("Yipe");
			// Create a new custom class loader, pointing to the directory that
			// contains the compiled
			// classes, this should point to the top of the package structure!
			URLClassLoader classLoader = new URLClassLoader(new URL[] { new File("./").toURI().toURL() });
			// Load the class from the classloader by name....
			Class<?> loadedClass = classLoader.loadClass(className);
			// Create a new instance...

			// Santity check 1
			if (loadedClass == null) {
				plugin.debugWarning("Instance was null");
			} else {
				plugin.debugInfo("Instance toString(): " + loadedClass.toString());
			}

			// Santity check 1
			Object obj = loadedClass.newInstance();
			if (obj == null) {
				plugin.debugWarning("obj was null");
			} else {
				plugin.debugInfo("obj toString(): " + obj.toString());
			}

			classLoader.close();
			/*************************************************************************************************
			 * Load and execute
			 **/
		} else {
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(),
						diagnostic.getSource().toUri());
				
				plugin.debugInfo("diagnostic message: " + diagnostic.getMessage(null));
				
			}
		}
		fileManager.close();
	}

}
