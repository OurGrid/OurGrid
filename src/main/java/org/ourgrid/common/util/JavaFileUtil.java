/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.ourgrid.common.exception.UnableToDigestFileException;
import org.ourgrid.common.executor.Win32Executor;
import org.ourgrid.worker.WorkerConfiguration;

import sun.misc.BASE64Encoder;

/**
 * This is a java file utils class, it has method to manipulate java file names.
 */
public class JavaFileUtil {

	/** This represents the class file name extension */
	private static final String CLASS_SUFFIX = ".class";

	/** This represents the java source file name extension */
	private static final String JAVA_SUFFIX = ".java";


	public static void writeToFile(String string, String path) throws IOException{
		
		File f = new File(path);
		FileWriter writer = new FileWriter(f, true);
		writer.write(string + System.getProperty( "newLine" ));
		writer.close();
		
	}
	
	/**
	 * This method extract the extension of the Java Source file name
	 * 
	 * @param namePlusSufix The coplete name to the Java Source File
	 * @param suffix The substring that should be extracted
	 * @return The file name without suffix parameter.
	 */
	public static String extractJavaSuffix( String namePlusSufix, String suffix ) {

		int sufixo_inicio = namePlusSufix.indexOf( suffix );
		if ( sufixo_inicio != -1 ) {
			namePlusSufix = namePlusSufix.substring( 0, sufixo_inicio );
		}
		return namePlusSufix;
	}


	/**
	 * This method determine the complete name of a class
	 * 
	 * @param file The file abstraction that denotes a java class file
	 * @param root The root directory where the class is located
	 * @return The complete name of a class
	 */
	public static String getFullClassName( File file, String root ) {

		String path = file.getAbsolutePath();

		// extracting root directory from pathname;
		int inicial = path.indexOf( root );
		inicial += root.length();
		inicial += 1;

		String pathPlusClassName = path.substring( inicial, path.length() );
		String classFullName = JavaFileUtil.extractJavaSuffix( pathPlusClassName, JavaFileUtil.CLASS_SUFFIX );
		classFullName = JavaFileUtil.extractJavaSuffix( classFullName, JavaFileUtil.JAVA_SUFFIX );
		classFullName = classFullName.replace( File.separatorChar, '.' );

		return classFullName;
	}


	/**
	 * That utility method get a File object in applying a Message Digest
	 * Filter, the result is a digest string representation of the file contents
	 * 
	 * @param fileToDigest The File object abstraction that denotes a file to be
	 *        digested
	 * @return The digest string representation of the file contents. Or null if
	 *         some exception occurs,
	 * @throws UnableToDigestFileException If there is any problem on the digest
	 *         generation, like the file is not found, I/O errors or the digest
	 *         algorithm is not valid.
	 */
	public static String getDigestRepresentation( File fileToDigest ) throws UnableToDigestFileException {

		/** *** Declarations **** */

		/*
		 * A message digest to process the input file and generate a hashcode
		 * for it
		 */
		MessageDigest messageDigest;

		/* The FileInputStream used to read the input file */
		FileInputStream inputStream = null;

		/* The size of each read of the input while generating the digest */
		byte[ ] buffer = new byte[ 8129 ];

		/* The number of bytes to be used on the digest update() */
		int numberOfBytes;

		/* An array of bytes representing the result digest value */
		byte[ ] digestValue;

		/* An encoder to convert the digest to a readable String */
		BASE64Encoder encoder;

		/* A readable representation of the digest */
		String fileHash = new String();

		/** *** Calculating the digest **** */

		try {
			messageDigest = MessageDigest.getInstance("MD5"); /*
																 * MD5 is the
																 * Message
																 * Digest
																 * Algorithm
																 */

			inputStream = new FileInputStream(fileToDigest.getAbsoluteFile());
			numberOfBytes = inputStream.read(buffer);

			while (numberOfBytes != -1) {
				messageDigest.update(buffer, 0, numberOfBytes);
				numberOfBytes = inputStream.read(buffer);
			}

			/* generating the digest */
			digestValue = messageDigest.digest();

			/* make the digest a readable string */
			encoder = new BASE64Encoder();
			fileHash = encoder.encode(digestValue);

		} catch (IOException exception) {
			throw new UnableToDigestFileException(fileToDigest.getAbsolutePath(), exception);
		} catch (NoSuchAlgorithmException exception) {
			throw new UnableToDigestFileException(fileToDigest.getAbsolutePath(), exception);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}

		return fileHash;

	}


	/**
	 * Tells if a given filepath represents an absolute path or not.
	 * 
	 * @param filepath The file path.
	 * @return True if the given filepath represents an absolute file path,
	 *         false otherwise.
	 */
	public static boolean isAbsolutePath(String filepath) {
		//FIXME: essa verifica��o � mesmo necess�ria ?
		if ((filepath.indexOf(":\\") != -1) || filepath.charAt(0) == '\\') {
			// TODO: Verify if the verification
			// for windows should be done this way
			return true;
		}
		return (new File(filepath)).isAbsolute();

	}


	/**
	 * Makes translations that depend on the operating system being used.
	 * 
	 * @param filepath The file path.
	 * @param gumAttributesMap A map of attributes from where we retrieve the
	 *        ATT_OS attribute. This attribute represents a constant indicating
	 *        the Operating System according to <code>GuMSpec</code>
	 *        constants.
	 * @return The translated filepath.
	 */
	public static String getTranslatedFilePath( String filepath, Map gumAttributesMap ) {

		// TODO Verify for other Operating Systems if the checking
		// is the same used for the home machine (linux).
		// FIXME: Check if WorkerConfiguration should be used here
		String os = (String) gumAttributesMap.get( WorkerConfiguration.ATT_OS );
		if ( os != null && os.equals( WorkerConfiguration.OS_WINDOWS ) ) {
			return (Win32Executor.convert2WinStyle( filepath ));
		}
		return filepath;
	}


	/**
	 * Deletes a directory and all of its contents recursively.
	 * 
	 * @param dir Directory to delete.
	 * @return True if directory was deleted.
	 */
	public static boolean deleteDir( File dir ) {

		if ( dir.isDirectory() && dir.exists() ) {
			String[ ] children = dir.list();
			for ( int i = 0; i < children.length; i++ ) {
				boolean success = deleteDir( new File( dir, children[i] ) );
				if ( !success ) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}


	/**
	 * Deletes a directory and all of its contents recursively.
	 * 
	 * @param mainDir Directory to delete.
	 * @return True if directory was deleted.
	 */
	public static boolean deleteDir( String string ) {

		return deleteDir( new File( string ) );
	}


	/**
	 * Algorithm used to copy files.
	 * 
	 * @param sourceFile Source file.
	 * @param destFile Destination file.
	 * @throws IOException In case an exception occurs while copying.
	 */
	public static void copyFile( File sourceFile, File destFile ) throws IOException {

		if ( !destFile.exists() ) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream( sourceFile ).getChannel();
			destination = new FileOutputStream( destFile ).getChannel();
			destination.transferFrom( source, 0, source.size() );
		} finally {
			if ( source != null ) {
				source.close();
			}
			if ( destination != null ) {
				destination.close();
			}
		}
	}

	/**
	 * @param srcPath
	 * @param dstPath
	 * @throws IOException
	 */
	public static void copyDirectory(String srcPath, String dstPath) throws IOException{
		copyDirectory(new File(srcPath), new File(dstPath));
	}
		  
	/**
	 * TODO: REFACTORING TO N.I.O
	 * @param srcPath
	 * @param dstPath
	 * @throws IOException
	 */
	public static void copyDirectory(File srcPath, File dstPath) throws IOException{
		
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}
			
			String files[] = srcPath.list();
			for(int i = 0; i < files.length; i++){
				copyDirectory(new File(srcPath, files[i]), new File(dstPath, files[i]));
			}
		}
		else {
			if(!srcPath.exists()){
				throw new IllegalArgumentException("File or directory does not exist.");
			}
			else{
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
	}

	public static boolean setWritable(File file) {
		try {
			new FilePermission(file.getAbsolutePath(), "write");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean setReadable(File file) {
		try {
			new FilePermission(file.getAbsolutePath(), "read");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean setExecutable(File file) {
		try {
			new FilePermission(file.getAbsolutePath(), "execute");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Algorithm used to copy files.
	 * 
	 * @param sourceFile Source file.
	 * @param destFile Destination file.
	 * @throws IOException In case an exception occurs while copying.
	 */
	public static void copyFile( String sourceFile, String destFile ) throws IOException {

		copyFile( new File( sourceFile ), new File( destFile ) );
	}
}
