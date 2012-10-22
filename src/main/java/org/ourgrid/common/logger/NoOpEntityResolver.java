package org.ourgrid.common.logger;

import java.io.StringBufferInputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class NoOpEntityResolver implements EntityResolver {
	public InputSource resolveEntity(String publicId, String systemId) {
		return new InputSource(new StringBufferInputStream(""));
	}
}

