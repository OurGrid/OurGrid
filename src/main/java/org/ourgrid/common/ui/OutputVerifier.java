package org.ourgrid.common.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class OutputVerifier {

	public static final String OUT_FILE = "start.output.tmp";

	private static final int SLEEP = 15000;
	private static final String SUCCESS = "successfully started";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static void main(String[] args) {
		boolean ok = false;
		StringBuffer buffer = new StringBuffer();
		File outputFile = new File(OUT_FILE);

		try {
			Thread.sleep(SLEEP);

			BufferedReader reader = new BufferedReader(new FileReader(outputFile));

			while (reader.ready()) {
				buffer.append(reader.readLine() + LINE_SEPARATOR);

				if (buffer.toString().contains(SUCCESS)) {
					ok = true;
				}
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.print(buffer);

		int exit = ok ? 0 : 1;
		System.exit(exit);
	}
}
