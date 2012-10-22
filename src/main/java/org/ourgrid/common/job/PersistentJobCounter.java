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
package org.ourgrid.common.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A <code>JobCounter</code> that starts count from a value persisted on a
 * given file. Every time the counter changes the id is persited on the file.
 * 
 * Requirement 302
 */
public class PersistentJobCounter implements JobCounter {

	private static final long serialVersionUID = 40L;

	/**
	 * Counter use to determine job-ids.
	 */
	private SimpleJobCounter simpleCounter;

	/**
	 * File to persist last job-id in.
	 */
	private final File counterFile;


	/**
	 * Creates a new <code>PersistentJobCounter</code> using the given file to
	 * persist the job-id.
	 * 
	 * @param counterFile File to persist id in.
	 * @throws IOException In cas file cannot be created or loaded.
	 */
	public PersistentJobCounter( String counterFile ) throws IOException {

		this( new File( counterFile ) );
	}


	/**
	 * Creates a new <code>PersistentJobCounter</code> using the given file to
	 * persist the job-id.
	 * 
	 * @param counterFile File to persist id in.
	 * @throws IOException In cas file cannot be created or loaded.
	 */
	public PersistentJobCounter( File counterFile ) throws IOException {

		this.counterFile = counterFile;

		if ( !counterFile.exists() ) { // Create new file.
			this.simpleCounter = new SimpleJobCounter();
		} else { // Load id from file
			loadID();
		}
	}


	/**
	 * Loads last known job-id from counterFile.
	 * 
	 * @throws IOException In case file cannot be read.
	 */
	private void loadID() throws IOException {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader( new FileReader( counterFile ) );
			String lastid = reader.readLine();
			int lastIdInteger;

			try {
				lastIdInteger = Integer.parseInt( lastid );
			} catch ( NumberFormatException e ) {
				lastIdInteger = 1;
			}

			this.simpleCounter = new SimpleJobCounter( lastIdInteger );

		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch ( IOException e ) {}
			}
		}
	}


	public int nextJobId() {
		return saveJobId();
	}


	public int getJobId() {

		return simpleCounter.getJobId();
	}


	/**
	 * @return The File that ths job is being persisted.
	 */
	public File getCounterFile() {

		return counterFile;
	}


	public void shutdown( boolean force ) {
		
		saveJobId();
		simpleCounter.shutdown( false );
	}


	private int saveJobId() {
		
		if ( counterFile.exists() ) {
			counterFile.delete();
		}

		try {
			counterFile.createNewFile();
		} catch ( IOException e ) {}

		PrintWriter writer = null;
		int nextJobId = simpleCounter.nextJobId();
		
		try {
			writer = new PrintWriter( counterFile );
			writer.println( Integer.toString( nextJobId ) );
		} catch ( FileNotFoundException e ) {
			// TODO LOG THIS
		} finally {
			if ( writer != null ) {
				writer.flush();
				writer.close();
			}
		}
		return nextJobId;
	}
}
