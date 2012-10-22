package org.ourgrid.peer.business.dao.statistics;

import org.ourgrid.common.statistics.beans.peer.Attribute;
import org.ourgrid.common.statistics.beans.peer.Worker;

public class EntityDAO {

	protected long now() {
		return System.currentTimeMillis();
	}
	
/*	protected String getAddress(ServiceID serviceID) {
		return serviceID.getUserName() + "@" + serviceID.getServerName();
	}*/
	
	protected Attribute createAttribute(Worker worker, String key, String value, boolean isAnnotation) {
		Attribute attribute = new Attribute();
		attribute.setProperty(key);
		attribute.setValue(value);
		attribute.setBeginTime(now());
		attribute.setIsAnnotation( isAnnotation );
		attribute.setEndTime(null);
		attribute.setLastModified(now());
		attribute.setWorker(worker);
		//worker.getAttributes().add(attribute);
		return attribute;
	}
	
	protected Attribute createAttribute(Worker worker, String key, String value) {
		return createAttribute(worker, key, value, false);
	}
}
