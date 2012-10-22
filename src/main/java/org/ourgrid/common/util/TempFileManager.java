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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Description: An utility class created to lead with temp files.
 * 
 * @version 1.0 Created on 27/07/2004
 */
public class TempFileManager {

	/**
	 * Creates a temp file with the format broker???.tmp in the default temp
	 * dir. (??? is a random long number.)
	 * 
	 * @return The temp file handler.
	 * @throws IOException If the temp file could not be created.
	 */
	public static File createTempFile() throws IOException {
		return createTempFile("broker", ".tmp");
	}


	/**
	 * Creates a temp file with the format prefix???sufix in the default temp
	 * dir. (??? is a random long number.)
	 * 
	 * @param prefix A prefix for the temp file.
	 * @param sufix A sufix for the temp file.
	 * @return The temp file handler.
	 * @throws IOException If the temp file could no be created.
	 */
	public static File createTempFile(String prefix, String sufix) throws IOException {

		return createTempFile(prefix, sufix, new File("/tmp"));
	}


	/**
	 * Creates a temp file with the format prefix???sufix in a specified dir.
	 * (??? is a random long number.)
	 * 
	 * @param prefix A prefix for the temp file.
	 * @param sufix A sufix for the temp file.
	 * @param dir The temp file directory
	 * @return The temp file handler.
	 * @throws IOException If the temp file could no be created.
	 */
	public static File createTempFile(String prefix, String sufix, File dir) throws IOException {
		long l = (long) (Math.random() * (Long.MAX_VALUE - 1));
		return createTempFile(prefix + l + sufix, dir);
	}
	
	public static void createTempDir(String dir) {
		createTempDir(new File(dir));
	}

	public static void createTempDir(File dir) {
		dir.mkdirs();
	}
	
	public static File createTempFile(String fileName, File dir) throws IOException{
		File f = null;
		createTempDir(dir);

		do {			
			f = new File( getFileName(dir, fileName));

		} while (!f.createNewFile());

		return f;		
	}
	
	public static String getFileName(File dir, String fileName){
		return dir.getAbsolutePath() + File.separatorChar + fileName;
	}


	/**
	 * Creates a temp file with the format prefix???sufix in a specified dir
	 * with a specified amount of bogus data. (??? is a random long number.)
	 * 
	 * @param prefix A prefix for the temp file.
	 * @param sufix A sufix for the temp file.
	 * @param dir The temp file directory
	 * @param nbytes The amount of bogus data to be wrote on the temp file (in
	 *        bytes).
	 * @return The temp file handler.
	 * @throws IOException If the temp file could no be created.
	 */

	public static File createTempFileWithBogusData(String prefix, String sufix, File dir, int nbytes) throws IOException {
		File f = createTempFile(prefix, sufix, dir);
		f.mkdirs();
		fillFile( nbytes, f );
		return f;

	}
	
	
	public static File createTempFileWithBogusData( String fileName, File dir, int nbytes) throws IOException {
		File f = createTempFile(fileName, dir);
		f.mkdirs();
		fillFile( nbytes, f );
		return f;
	}
	
	
	public static File createTempFileWithBogusData( String name, int nbytes ) throws IOException {

		File f = File.createTempFile( name, null );
		fillFile( nbytes, f );
		return f;

	}
	
	
	private static void fillFile( int nbytes, File f ) throws IOException {

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(f));

			for (int i = 0; i < nbytes; i++)
				writer.write(i % 128);

		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
