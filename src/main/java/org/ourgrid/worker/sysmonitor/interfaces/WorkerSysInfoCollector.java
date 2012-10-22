package org.ourgrid.worker.sysmonitor.interfaces;

import java.util.Map;

import br.edu.ufcg.lsd.commune.api.Remote;

/**
 * 
 * This interface is responsible to receive information when the Worker´s Metrics change.
 *
 */
@Remote
public interface WorkerSysInfoCollector {

	/**
	 * Called when the Worker´s  metrics change.
	 * @param metricsMap metrics Map.
	 */
	public void metricsChanged(Map<String,String> metricsMap);
	
}
