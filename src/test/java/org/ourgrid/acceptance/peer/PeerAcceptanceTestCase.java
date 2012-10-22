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
package org.ourgrid.acceptance.peer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.exception.JobSpecificationException;
import org.ourgrid.common.specification.exception.TaskSpecificationException;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.JDLCompiler;
import org.ourgrid.common.specification.main.CommonCompiler.FileType;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestCase;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;


public class PeerAcceptanceTestCase extends AcceptanceTestCase {

	public static final String SEP = File.separator;
	public static final String ACCEPTANCE_TEST_DIR = "test" + SEP + "acceptance" + SEP;
	public static final String PEER_TEST_DIR = ACCEPTANCE_TEST_DIR + "peer";
	private static final String COMM_FILE_PATH = "test"+File.separator+"acceptance"+File.separator+"req_011";
	public static final String PEER_PROP_FILEPATH = PEER_TEST_DIR + SEP + "peer.properties";

	public static final String MACHINE1 = ACCEPTANCE_TEST_DIR.concat( "file1.classad" );
	public static final String MACHINE2 = ACCEPTANCE_TEST_DIR.concat( "file2.classad" );
	public static final String MACHINE3 = ACCEPTANCE_TEST_DIR.concat( "file3.classad" );
	public static final String MACHINE4 = ACCEPTANCE_TEST_DIR.concat( "file4.classad" );

	public static final String buildRequirements(String memOperator, Integer memValue, String osOperator, String osValue){
		StringBuilder requirements = new StringBuilder("[Requirements = ");
		if(memOperator != null || memValue != null){
			requirements.append( "other.MainMemory " );
			requirements.append( memOperator );
			requirements.append( " " );
			requirements.append( memValue );
			if(osValue != null){
				requirements.append( " && " );
			}else{
				requirements.append( ";" );
			}
		}
		if(osValue != null){
			requirements.append( "other.OS ");
			requirements.append( osOperator );
			requirements.append( " \"" );
			requirements.append( osValue );
			requirements.append( "\";");
		}
		if(memOperator == null && memValue == null && osValue == null){
			requirements.append( "TRUE;");
		}
		requirements.append( "Rank=0]");
		return requirements.toString();
	}

	public static final String buildRequirements(String expression){
		StringBuilder requirements = new StringBuilder("[Requirements = ");
		if(expression != null){
			requirements.append( expression );
		}else{
			requirements.append( "TRUE");
		}
		requirements.append( ";Rank=0]");
		return requirements.toString();
	}


	public static final String buildRequirementsAndRank(String reqExpression, String rankExpression){
		StringBuilder expression = new StringBuilder("[Requirements = ");
		if(reqExpression != null){
			expression.append( reqExpression );
		}else{
			expression.append("TRUE");
		}
		expression.append(";Rank=");
		if(rankExpression != null){
			expression.append( rankExpression );
		}else{
			expression.append( "0");
		}
		
		expression.append( "]");
		return expression.toString();
	}

	protected PeerAcceptanceUtil peerAcceptanceUtil = new PeerAcceptanceUtil(getComponentContext());

    @BeforeClass
    public static void recreateSchema() {
    	//PeerAcceptanceUtil.recreateSchema();
    }

    @Before
    public void setUp() throws Exception {
        PeerAcceptanceUtil.setUp();
        super.setUp();
    }
    
    public ObjectDeployment getPeerControlDeployment(PeerComponent component) {
    	return component.getObject(Module.CONTROL_OBJECT_NAME);
    }
   
    @After
    public void tearDown() throws Exception {
        PeerAcceptanceUtil.tearDown();
    }

    
	/**
	 * Assumes that PeerConfiguration.PROP_DS_NETWORK contains a single address 
	 * @return DiscoveryService component user name 
	 */
	protected String getDSUserName() {
		return getComponentContext().getProperty(PeerConfiguration.PROP_DS_NETWORK).split("@")[0];
	}
	
	/**
	 * Assumes that PeerConfiguration.PROP_DS_NETWORK contains a single address 
	 * @return DiscoveryService component server name
	 */
	protected String getDSServerName() {
		return getComponentContext().getProperty(PeerConfiguration.PROP_DS_NETWORK).split("@")[1];
	}
	
	protected void copyTrustFile(String fileName) throws IOException {
		File origFile = new File(getRootForTrustFile()+File.separator+fileName);
		FileUtils.copyFile(origFile, 
				new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME));
	}

	protected String getRootForTrustFile() {
		return COMM_FILE_PATH;
	}

	@Override
	protected TestContext createComponentContext() {
		return new TestContext(
				new PeerComponentContextFactory(
						new PropertiesFileParser(PEER_PROP_FILEPATH
						)).createContext());
	}
	
	public DeploymentID createWorkerDeploymentID(WorkerSpecification workerSpec, String publicKey) {
		String user = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String server = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		DeploymentID workerDeploymentID = new DeploymentID(new ContainerID(user, server, WorkerConstants.MODULE_NAME, publicKey), 
				WorkerConstants.WORKER);
		
		return workerDeploymentID;
	}
	
	protected JobSpecification createJobSpec(String label) throws TaskSpecificationException,
	JobSpecificationException {
		return createJobSpec(label, 1);
	}
	
	protected JobSpecification createJobSpec(String label, int numOfTasks) throws TaskSpecificationException,
	JobSpecificationException {
		JobSpecification jobSpec = new JobSpecification(label);
		List<TaskSpecification> taskList = new ArrayList<TaskSpecification>();
		for (int i = 0; i < numOfTasks; i++) {
			taskList.add(new TaskSpecification(new IOBlock(), "echo test", new IOBlock(), null));
		}
		jobSpec.setTaskSpecs(taskList);
		return jobSpec;
	}
	
	protected JobSpecification createJobSpecJDL(String jobPath, int numOfTasks) throws TaskSpecificationException,
	JobSpecificationException, CompilerException {
		JDLCompiler jdlCompiler = new JDLCompiler();
		jdlCompiler.compile(jobPath, FileType.JDL);
		JobSpecification jobSpec = (JobSpecification) jdlCompiler.getResult().get(0);
		
		List<TaskSpecification> taskList = new ArrayList<TaskSpecification>(jobSpec.getTaskSpecs());
		for (int i = 0; i < numOfTasks - jobSpec.getTaskSpecs().size(); i++) {
			taskList.add(new TaskSpecification(new IOBlock(), "echo test", new IOBlock(), null));
		}
		jobSpec.setTaskSpecs(taskList);
		return jobSpec;
	}
	
	protected JobSpecification createJobSpecJDL(String jobPath) throws TaskSpecificationException,
	JobSpecificationException, CompilerException {
		return createJobSpecJDL(jobPath, 0);
	}
	
	protected JobSpecification createJobSpec(String label, String requirements) throws TaskSpecificationException,
	JobSpecificationException {
		JobSpecification createdJobSpec = createJobSpec(label);
		createdJobSpec.setRequirements(requirements);
		return createdJobSpec;
	}
	
	protected String getPeerAddress() {
		
		String user = getComponentContext().getProperty(XMPPProperties.PROP_USERNAME);
	    String server = getComponentContext().getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
	    
	    return user + "@" + server;
	}

}