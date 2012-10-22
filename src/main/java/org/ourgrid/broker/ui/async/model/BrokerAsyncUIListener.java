package org.ourgrid.broker.ui.async.model;

import java.util.List;

import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;

public interface BrokerAsyncUIListener {

	public void brokerStarted();

	public void brokerStopped();

	public void jobHistoryUpdated(List<String> jobHistory);

	public void updateCompleteStatus(BrokerCompleteStatus status);

	public void brokerInited();

	public void brokerInitedFailed();

	public void brokerRestarted();
	
	public void brokerEditing();
}
