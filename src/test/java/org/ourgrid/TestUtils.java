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
package org.ourgrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

public class TestUtils {

	public static final String SYSTEM_TEMP_DIR = "system.temp.dir";

	public static final String FAKE_FILE = "fake.file";

	private Properties testProperties;

	private static final String DEFAULT_TEST_PROPERTIES_FILENAME = "test" + File.separatorChar + "resources"
			+ File.separatorChar + "test.properties";

	public static final String PROTECTED_DIR = "protected.dir";

	public static final String REMOTE_PROTECTED_DIR = "remote.protected.dir";

	public static final String REMOTE_FILE = "remote.file";

	private static final String SH = "sh";


	/**
	 * Loads the tests properties from the default file.
	 */
	public void load() {

		load( DEFAULT_TEST_PROPERTIES_FILENAME );
	}


	/**
	 * Loads the tests properties from the specified file.
	 * 
	 * @param fileName The file name.
	 */
	public void load( String fileName ) {

		this.testProperties = new Properties();

		FileInputStream fileInputStream;

		try {
			fileInputStream = new FileInputStream( fileName );
			testProperties.load( fileInputStream );
		} catch ( IOException e ) {
			System.err.println( "Could not load test properties." );
			e.printStackTrace();
			System.exit( ErrorCode.COULD_NOT_LOAD_TEST_PROPERTIES );
		}

	}


	/**
	 * Resets all data.
	 */
	public void reset() {

		testProperties = new Properties();
	}


	/**
	 * Returns the property specified.
	 * 
	 * @param propertyKey The property key.
	 * @return Returns the property value or <code>null</code> case the
	 *         property was not previously specified.
	 */
	public String getProperty( String propertyKey ) {

		return testProperties.getProperty( propertyKey );
	}


	/**
	 * Sets a property.
	 * 
	 * @param propertyKey The property key.
	 * @param propertyValue The property value.
	 */
	public void setProperty( String propertyKey, String propertyValue ) {

		testProperties.setProperty( propertyKey, propertyValue );
	}


	public void createProperties( String originalFileName, String backUpFileName, String prefix ) {

		File prop = new File( originalFileName );
		File propBackup = new File( backUpFileName );
		prop.renameTo( propBackup );
		Properties props = new Properties();

		try {
			loadPropertiesInto( props, prefix );
			FileOutputStream outputStream = new FileOutputStream( originalFileName );
			props.store( outputStream, "New " + originalFileName );
			outputStream.close();
		} catch ( Exception e ) {
			System.err.println( "Could not create a new " + backUpFileName + "." );
			e.printStackTrace();
		}
	}


	/**
	 * Loads the properties referring to MG.properties from the
	 * <code>TestUtils#DEFAULT_TEST_PROPERTIES_FILENAME</code>.
	 * 
	 * @param props Where the properties will be loaded into.
	 * @param prefix The prefix of the properties that will be loaded into.
	 */
	private void loadPropertiesInto( Properties props, String prefix ) {

		Iterator<Object> iterator = testProperties.keySet().iterator();
		while ( iterator.hasNext() ) {
			String propertyKey = (String) iterator.next();
			if ( propertyKey.startsWith( prefix ) ) {
				props.setProperty( propertyKey, testProperties.getProperty( propertyKey ) );
			}
		}
	}


	/**
	 * Runs a command.
	 * 
	 * @param commands The command.
	 * @param timeWaiting The time to sleep after the command was performed -
	 *        not recommended to use.
	 * @throws Exception Case there's any problem.
	 */
	public static void runCommand( String[ ] commands, long timeWaiting ) throws Exception {

		String command = "";
		for ( int k = 0; k < commands.length; k++ ) {
			command += commands[k] + " ";
		}

		System.out.println( "Executing: " + command );

		Runtime exec = Runtime.getRuntime();
		Process process = exec.exec( commands );
		StreamGobbler streamGobblerStd = new StreamGobbler( process.getInputStream(), "STD" );
		streamGobblerStd.start();
		StreamGobbler streamGobblerErr = new StreamGobbler( process.getErrorStream(), "ERROR" );
		streamGobblerErr.start();

		int exitValue = process.waitFor();

		if ( exitValue != 0 ) {
			System.err.println( "Finished (with error): " + command );
			throw new Exception( command + "exit with value " + exitValue );
		}
		Thread.sleep( timeWaiting );
		System.out.println( "Finished: " + command );
	}


	public boolean checkFileContent( String expectedContent, File file ) throws Exception {

		BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
		String line = bufferedReader.readLine();
		String contentInFile = "";
		while ( line != null ) {
			contentInFile += line;
			line = bufferedReader.readLine();
		}
		System.out.println( "expected: " + expectedContent );
		System.out.println( "obtained: " + contentInFile );
		bufferedReader.close();
		return expectedContent.equals( contentInFile );
	}


	/**
	 * Executes a command with its arguments.
	 * 
	 * @param command The command to be executed.
	 * @param args Its arguments.
	 * @throws Exception Case the command cannot be executed.
	 */
	public static void executeCommand( String command, String[ ] args ) throws Exception {

		String[ ] commandArray = new String[ args.length + 2 ];
		commandArray[0] = SH;
		commandArray[1] = command;
		for ( int k = 0; k < args.length; k++ ) {
			commandArray[k + 2] = args[k];
		}
		TestUtils.runCommand( commandArray, 0 );
	}

}


class StreamGobbler extends Thread {

	InputStream is;

	String type;


	StreamGobbler( InputStream is, String type ) {

		this.is = is;
		this.type = type;
	}


	@Override
	public void run() {

		try {
			InputStreamReader isr = new InputStreamReader( is );
			BufferedReader br = new BufferedReader( isr );
			String line = null;
			while ( (line = br.readLine()) != null )
				System.out.println( type + "> " + line );
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
}
