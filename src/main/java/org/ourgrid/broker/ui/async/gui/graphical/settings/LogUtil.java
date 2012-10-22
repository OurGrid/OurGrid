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
package org.ourgrid.broker.ui.async.gui.graphical.settings;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LogUtil {

	private static final String FILESIZE_PATTERN = "%FILESIZE%";

	private static final String LEVEL_PATTERN = "%LEVEL%";

	private static final String FILES_TO_KEEP_PATTERN = "%FILESTOKEEP%";


	public static void prepareMgLogProperties( boolean enable, String logLevel, int fileSize, int filesToKeep,
												String templateFile, String destinationFile )
		throws FileNotFoundException, IOException {

		/*
		StringBuilder sb = JavaFileUtil.fileToStringBuilder( templateFile );

		sb = enable	? new StringBuilder( Pattern.compile( LEVEL_PATTERN ).matcher( sb ).replaceFirst( logLevel ) )
					: new StringBuilder( Pattern.compile( LEVEL_PATTERN ).matcher( sb ).replaceFirst( "OFF" ) );

		sb = new StringBuilder( Pattern.compile( FILESIZE_PATTERN ).matcher( sb ).replaceFirst(
			Integer.toString( fileSize ) ) );

		sb = new StringBuilder( Pattern.compile( FILES_TO_KEEP_PATTERN ).matcher( sb ).replaceFirst(
			Integer.toString( filesToKeep ) ) );

		JavaFileUtil.stringBuilderToFile( destinationFile, sb );
		*/
	}


	public static void preparePeerLogProperties( boolean enable, String logLevel, String templateFile,
													String destinationFile ) throws FileNotFoundException, IOException {
/*
		StringBuilder sb = JavaFileUtil.fileToStringBuilder( templateFile );

		sb = enable	? new StringBuilder( Pattern.compile( LEVEL_PATTERN ).matcher( sb ).replaceAll( logLevel ) )
					: new StringBuilder( Pattern.compile( LEVEL_PATTERN ).matcher( sb ).replaceAll( "OFF" ) );

		JavaFileUtil.stringBuilderToFile( destinationFile, sb );
		*/
	}
}
