package org.ourgrid.discoveryservice.communication.sender;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.config.PersistNetworkUtil;
import org.ourgrid.discoveryservice.response.PersistNetworkResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PersistNetworkSender implements SenderIF<PersistNetworkResponseTO>{


	public void execute(PersistNetworkResponseTO response,
			ServiceManager manager) {
		
		try {
			PersistNetworkUtil.getInstance().persistNetwork(
					parseServiceAddressToUserAtServerAddress(response.getDiscoveryServicesAddresses()), 
					manager.getContainerContext());
		} catch (IOException e) {
			manager.getLog().error("Properties file could not be written with new Discovery Services Address", e);
		}
	}

	private Set<String> parseServiceAddressToUserAtServerAddress(Set<DiscoveryServiceInfo> dsServiceAddressList){
		
		Set<String> userAtServerList = new LinkedHashSet<String>();
		
		for (DiscoveryServiceInfo ds : dsServiceAddressList) {
			String dsServiceAddress = ds.getDsAddress();
			userAtServerList.add(ServiceID.parse(dsServiceAddress).getContainerID().getUserAtServer());
		}
		
		return userAtServerList;
	}
	
}
