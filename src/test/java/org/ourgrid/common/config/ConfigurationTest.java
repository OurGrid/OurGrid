package org.ourgrid.common.config;

import junit.framework.Assert;

import org.junit.Test;
import org.ourgrid.system.config.FakeConfiguration;

public class ConfigurationTest {

	@Test
	public void testGetProperty() {
		Configuration conf = new FakeConfiguration();
		conf.setProperty("worker.publicKey", "ABC");
		Assert.assertEquals("ABC", conf.getProperty("worker.publicKey"));
		
		conf.setProperty("publicKey", "DEF");
		Assert.assertEquals("ABC", conf.getProperty("worker.publicKey"));
	}
	
	@Test
	public void testGetPropertyNoPrefix() {
		Configuration conf = new FakeConfiguration();
		conf.setProperty("publicKey", "DEF");
		Assert.assertEquals("DEF", conf.getProperty("worker.publicKey"));
	}

}
