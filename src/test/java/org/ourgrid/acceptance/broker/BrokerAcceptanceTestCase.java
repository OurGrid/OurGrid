/*
 * Copyright (c) 2002-2007 Universidade Federal de Campina Grande This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ourgrid.acceptance.broker;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerComponentContextFactory;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.common.specification.peer.PeerSpecification;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestCase;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

public class BrokerAcceptanceTestCase extends AcceptanceTestCase {

	public static final String SEP = File.separator;
	public static final String BROKER_TEST_DIR = "test" + SEP + "acceptance" + SEP + "broker" + SEP;
	public static final String PROPERTIES_FILENAME = BROKER_TEST_DIR + "broker.properties";
	
	@Before
    public void setUp() throws Exception {
       BrokerAcceptanceUtil.setUp();
    }
	
	@After
    public void tearDown() throws Exception {
		BrokerAcceptanceUtil.tearDown();
    }
	
	@Override
	protected TestContext createComponentContext() {
		return new TestContext(
				new BrokerComponentContextFactory(
						new PropertiesFileParser(PROPERTIES_FILENAME
						)).createContext());
	}

	protected PeerSpecification getPeerSpec() {
		PeerSpecification peerSpec = new PeerSpecification();
		peerSpec.setUserAtServer(getComponentContext().getProperty(
				BrokerConfiguration.PROP_PEER_USER_AT_SERVER));
		return peerSpec;
	}

}