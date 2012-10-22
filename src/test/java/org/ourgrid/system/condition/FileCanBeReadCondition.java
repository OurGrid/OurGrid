/* Created at 13/12/2006 */

package org.ourgrid.system.condition;

import java.io.File;

public class FileCanBeReadCondition implements Condition {

	private final File file;


	public FileCanBeReadCondition( String file ) {

		this.file = new File( file );
	}


	public FileCanBeReadCondition( File file ) {

		this.file = file;
	}


	public boolean isConditionMet() throws Exception {

		if ( file.exists() ) {
			return file.canRead();
		}
		return false;
	}


	public String detailMessage() {

		return "File " + file.getPath() + "  could not be read";
	}

}
