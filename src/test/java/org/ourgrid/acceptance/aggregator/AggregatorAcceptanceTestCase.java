package org.ourgrid.acceptance.aggregator;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.util.AggregatorAcceptanceUtil;
import org.ourgrid.aggregator.AggregatorComponentContextFactory;
import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestCase;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

public class AggregatorAcceptanceTestCase extends AcceptanceTestCase {
	
	public static final String SEP = File.separator;
	public static final String AGGREGATOR_TEST_DIR = "test" + SEP + "acceptance" + SEP + "aggregator";
	private static final String PROPERTIES_FILENAME = AGGREGATOR_TEST_DIR + SEP + "aggregator.properties";
	protected AggregatorAcceptanceUtil aggAccept = new AggregatorAcceptanceUtil(getComponentContext()); 
	
	@Override
	protected TestContext createComponentContext() {
		return new TestContext(
				new AggregatorComponentContextFactory(
						new PropertiesFileParser(PROPERTIES_FILENAME
						)).createContext());
	}
	
	@Before
    public void setUp() throws Exception {
       AggregatorAcceptanceUtil.setUp();
       super.setUp();
    }
	
	@After
    public void tearDown() throws Exception {
		AggregatorAcceptanceUtil.tearDown();
		AggregatorDAOFactory.getInstance().reset();
    }
	

}
