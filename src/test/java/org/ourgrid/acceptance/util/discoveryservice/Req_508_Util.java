package org.ourgrid.acceptance.util.discoveryservice;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceReceiver;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_508_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_508_Util(ModuleContext context) {
		super(context);
	}

	public void doNotifyFailure(DiscoveryServiceComponent component, TestStub dscClientTestStub) {
		DiscoveryServiceReceiver controller = (DiscoveryServiceReceiver) getDiscoveryServiceDeployment(component).getObject();
		controller.dsClientIsDown((DiscoveryServiceClient)dscClientTestStub.getObject(), dscClientTestStub.getDeploymentID());
	}
	
	public void doNotifyFailureWithNullID(DiscoveryServiceComponent component, TestStub dscClientTestStub) {
		CommuneLogger logger = component.getLogger();
		DiscoveryServiceReceiver controller = (DiscoveryServiceReceiver) getDiscoveryServiceDeployment(component).getObject();
		DiscoveryServiceClient dsc = (DiscoveryServiceClient) dscClientTestStub.getObject();
		logger.error("Client ID invalid: null");
		EasyMock.replay(logger);
		controller.dsClientIsDown(dsc, null);
		EasyMock.verify(logger);

		EasyMock.reset(logger);
	}

	public void doNotifyFailureWithNonLoggedClient(DiscoveryServiceComponent component, TestStub dscClientTestStub) {
		CommuneLogger logger = component.getLogger();

		DiscoveryServiceReceiver controller = (DiscoveryServiceReceiver) getDiscoveryServiceDeployment(component).getObject();

		DiscoveryServiceClient dsc = (DiscoveryServiceClient) dscClientTestStub.getObject();

		logger.error("The client [" + dscClientTestStub.getDeploymentID().getContainerID().getUserAtServer() + "] is not logged.");
		EasyMock.replay(logger);

		controller.dsClientIsDown(dsc, dscClientTestStub.getDeploymentID());

		EasyMock.verify(logger);

		EasyMock.reset(logger);
		
	}

	
}

