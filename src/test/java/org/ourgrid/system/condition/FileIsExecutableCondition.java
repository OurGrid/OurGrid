/* Created at 13/12/2006 */

package org.ourgrid.system.condition;

import java.io.File;

import org.ourgrid.system.OurGridTestCase;

public class FileIsExecutableCondition implements Condition {

	private final File file;


	public FileIsExecutableCondition( String file ) {

		this.file = new File( file );
	}


	public FileIsExecutableCondition( File file ) {

		this.file = file;
	}


	public boolean isConditionMet() throws Exception {

		if ( file.exists() && file.canRead() ) {
			return OurGridTestCase.isFileExecutable( file.getAbsolutePath() );
		}
		return false;
	}


	public String detailMessage() {

		return "File " + file.getPath() + "  could not be executed";
	}
}
