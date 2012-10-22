package org.ourgrid.acceptance.util;

import java.io.File;
import java.io.FilePermission;

public class TestJavaFileUtil {
	
	public static boolean setReadAndWrite(File file) {
		return file.setReadable(true, false) && file.setWritable(true, false);
	}
	
	public static boolean setNonReadable(File file) {
		return file.setReadable(false, false);
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
}
