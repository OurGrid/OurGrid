package org.ourgrid.acceptance.util;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.discoveryservice.DiscoveryServiceAcceptanceTestCase;
import org.ourgrid.acceptance.discoveryservice.DiscoveryServiceAcceptanceTestComponent;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControl;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceNotificationReceiver;
import org.ourgrid.discoveryservice.config.DiscoveryServiceConfiguration;
import org.ourgrid.discoveryservice.config.PersistNetworkUtil;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.TestObjectsRegistry;

public class DiscoveryServiceAcceptanceUtil extends AcceptanceUtil {
	
	private static final String CONF_XML_PATH = "ds-hibernate.cfg.xml";
	
	public DiscoveryServiceAcceptanceUtil(ModuleContext context){
		super(context);
	}
	
	@Before
	public static void setUp() throws Exception{
		System.setProperty("OGROOT", ".");
        Configuration.getInstance(DiscoveryServiceConfiguration.DISCOVERY_SERVICE);
        PersistNetworkUtil.getInstance().setPropertiesFileName(
        		DiscoveryServiceAcceptanceTestCase.DS_PROP_FILEPATH);
        
        if (HibernateUtil.getSessionFactory() == null) {
    		HibernateUtil.setUp(CONF_XML_PATH);
		}

        HibernateUtil.recreateSchema();
	}
	
	@After
    public static void tearDown() throws Exception {
		if (application != null && !application.getContainerDAO().isStopped()) {
			application.stop();
		}
		TestObjectsRegistry.reset();
		
		File propertiesFile = new File(DiscoveryServiceAcceptanceTestCase.DS_PROP_FILEPATH);
	       
        if (propertiesFile.exists()) {
        	propertiesFile.delete();
        }
    }
	
	public DiscoveryServiceComponent createDiscoveryServiceComponent() throws CommuneNetworkException, ProcessorStartException, InterruptedException{
		application = new DiscoveryServiceAcceptanceTestComponent(context);
		
		Thread.sleep(2000);
		
		return (DiscoveryServiceComponent) application;
	}
	
    public DiscoveryServiceControl getDiscoveryServiceControl(DiscoveryServiceComponent component) {
        ObjectDeployment deployment = getDiscoveryServiceControlDeployment(component);
		return (DiscoveryServiceControl) deployment.getObject();
    }
    
    public ObjectDeployment getDiscoveryServiceControlDeployment(DiscoveryServiceComponent component) {
    	return component.getObject(Module.CONTROL_OBJECT_NAME);
    }

    public DiscoveryService getDiscoveryServiceProxy() {
        ObjectDeployment deployment = getDiscoveryServiceObjectDeployment();
		return (DiscoveryService) deployment.getObject();
    }
    
    public ObjectDeployment getDiscoveryServiceObjectDeployment() {
        return getTestProxy(application, DiscoveryServiceConstants.DS_OBJECT_NAME);
    }
    
    public ObjectDeployment getDiscoveryServiceDeployment(DiscoveryServiceComponent component) {
    	return component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
    }
    
    public CommunityStatusProvider getCommunityStatusProviders(DiscoveryServiceComponent component) {
    	ObjectDeployment objectDeployment = getCommunityStatusProvidersObjectDeployment(component);
    	return (CommunityStatusProvider) objectDeployment.getObject();
    }

    public ObjectDeployment getCommunityStatusProvidersObjectDeployment(DiscoveryServiceComponent component) {
    	return  component.getObject(DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER);
    }
    
    public ObjectDeployment 
    getDiscoveryServiceClientFailureControllerObjectDeployment(DiscoveryServiceComponent component) {
    	return  component.getObject(DiscoveryServiceConstants.DS_CLIENT_MONITOR);
    }
    
    public ObjectDeployment 
    getDiscoveryServiceMonitorDeployment() {
    	return getContainerObject(application, DiscoveryServiceConstants.DS_MONITOR);
    }
    
    public DiscoveryServiceNotificationReceiver
    getDiscoveryServiceNotificationReceiver() {
    	ObjectDeployment objectDeployment = getDiscoveryServiceMonitorDeployment();
    	return (DiscoveryServiceNotificationReceiver) objectDeployment.getObject();
    }
    
    
}
