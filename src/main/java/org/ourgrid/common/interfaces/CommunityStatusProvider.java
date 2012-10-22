package org.ourgrid.common.interfaces;

import br.edu.ufcg.lsd.commune.api.Remote;

/**
 * Provides an interface to update the status from the Peers in the community.
 */
@Remote
public interface CommunityStatusProvider {
	
	/**
	 * Get the actual status of all PeerStatusProviders.
	 * @param client The client of CommunityStatusProvider.
	 */
	void getPeerStatusProviders(CommunityStatusProviderClient client);
	
	/**
	 * Get the PeerStatus change history based on a given time. 
	 * @param client The client of CommunityStatusProvider.
	 * @param since The time to base the search on the PeerStatus change historical.
	 */
	void getPeerStatusChangeHistory(CommunityStatusProviderClient client, long since);
	
}
