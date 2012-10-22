package org.ourgrid.acceptance.discoveryservice;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.discoveryservice.DiscoveryServiceComponentContextFactory;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestCase;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

/**
 * Requirement 501
 */
public class DiscoveryServiceAcceptanceTestCase extends AcceptanceTestCase {

	public static final String SEP = File.separator;
	public static final String DS_TEST_DIR = "test" + SEP + "acceptance" + SEP + "discoveryservice";
	public static final String DS_PROP_FILEPATH = DS_TEST_DIR + SEP + "ds.properties";

	protected DiscoveryServiceAcceptanceUtil dsAcceptanceUtil = new DiscoveryServiceAcceptanceUtil(getComponentContext());
	
	@Override
	protected TestContext createComponentContext() {
		return new TestContext(
				new DiscoveryServiceComponentContextFactory(
						new PropertiesFileParser(DS_PROP_FILEPATH
						)).createContext());
	}
	
	@Before
	 public void setUp() throws Exception {
		DiscoveryServiceAcceptanceUtil.setUp();
        super.setUp();
    }

	@After
	public void tearDown() throws Exception{
		DiscoveryServiceAcceptanceUtil.tearDown();
	}
	
}
