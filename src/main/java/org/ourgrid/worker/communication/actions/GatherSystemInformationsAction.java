package org.ourgrid.worker.communication.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.sysmonitor.core.SysInfoGatheringCore;
import org.ourgrid.worker.sysmonitor.interfaces.WorkerSysInfoCollector;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;


public class GatherSystemInformationsAction implements RepeatedAction {

	private static final String UNKNOWN_VALUE = null;
	private SysInfoGatheringCore gatheringCore;
	private Map<String, String> systemInformations = CommonUtils.createSerializableMap();
	
	public GatherSystemInformationsAction() {
		SysInfoGatheringCore.loadLibraries();
		this.gatheringCore = new SysInfoGatheringCore();
		this.systemInformations = CommonUtils.createSerializableMap();
		this.getStaticInformations();
	}
	
	public void run(Serializable handler, ServiceManager serviceManager) {
		ObjectDeployment objectDeployment = serviceManager.getObjectDeployment(WorkerConstants.WORKER_SYSINFO_COLLECTOR);
		
		if(objectDeployment == null) return;
			
		WorkerSysInfoCollector controller = (WorkerSysInfoCollector) objectDeployment.getProxy();
		
		gatheringCore.gather();
		putAttribute(WorkerSpecificationConstants.CPU_IDLE_TIME, format(gatheringCore.getCpuIdle()));
		putAttribute(WorkerSpecificationConstants.CPU_USER_TIME, format(gatheringCore.getCpuUser()));
		putAttribute(WorkerSpecificationConstants.CPU_SYS_TIME, format(gatheringCore.getCpuSys()));
		putAttribute(WorkerSpecificationConstants.CPU_NICE_TIME, format(gatheringCore.getCpuNice()));
		putAttribute(WorkerSpecificationConstants.CPU_WAIT_TIME, format(gatheringCore.getCpuWait()));
		putAttribute(WorkerSpecificationConstants.CPU_USED_TOTAL_TIME, format(gatheringCore.getCpuCombined()));
		putAttribute(WorkerSpecificationConstants.CPU_LOAD, format(gatheringCore.getLoadAvarage()));
		putAttribute(WorkerSpecificationConstants.DISK_AVAIL, format(gatheringCore.getDiskAvail()));
		putAttribute(WorkerSpecificationConstants.FREE_MAIN_MEMORY, format(gatheringCore.getMemFree()));
		putAttribute(WorkerSpecificationConstants.FREE_PERCENT_OF_MAIN_MEMORY, format(gatheringCore.getMemFreePercent()));
		putAttribute(WorkerSpecificationConstants.CPU_PERC_SYS_ONLY, format(gatheringCore.getCpuPercSystemOnly()));
		
		controller.metricsChanged(systemInformations);
	}

	private void putAttribute(String key, String value) {

		if(value != null){
			systemInformations.put(WorkerSpecificationConstants.CPU_IDLE_TIME, format(gatheringCore.getCpuIdle()));
		}
	}

	private String format(double[] list) {
		if(list == null) return UNKNOWN_VALUE;
		List<Double> out = new ArrayList<Double>(list.length);
		for (double d : list) {
			out.add(d);
		}
		return out.toString();
	}

	private String format(int integer) {
		return (integer == -1)? UNKNOWN_VALUE : String.valueOf(integer);
	}

	private String format(double d) {
		return (d < 0.0 )? UNKNOWN_VALUE : String.valueOf(d); 

	}

	private String format(long integer) {
		return (integer == -1)? UNKNOWN_VALUE : String.valueOf(integer);
	}
	
	/*
	 * Retrieves unchangeable informations about the current system session. 
	 */
	private void getStaticInformations() {
		putAttribute(WorkerSpecificationConstants.CPU_CORES, format(gatheringCore.getCpuTotalCores()));
		putAttribute(WorkerSpecificationConstants.CPU_CLOCK, format(gatheringCore.getCpuMhz()));
		putAttribute(WorkerSpecificationConstants.CPU_MODEL, gatheringCore.getCpuModel());
		putAttribute(WorkerSpecificationConstants.CPU_VENDOR, gatheringCore.getCpuVendor());
		putAttribute(WorkerSpecificationConstants.FILE_SYSTEM_DIR_NAME, gatheringCore.getInstallationPartitionName());
		putAttribute(WorkerSpecificationConstants.FILE_SYSTEM_TYPE, gatheringCore.getFileSystemType());
		putAttribute(WorkerSpecificationConstants.DISK_TOTAL, format(gatheringCore.getDiskTotal()));
		putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, format(gatheringCore.getMemTotal()));
		putAttribute(WorkerSpecificationConstants.OS, gatheringCore.getOSName());
		putAttribute(WorkerSpecificationConstants.OS_DESCRIPTION, gatheringCore.getOSDescription());
		putAttribute(WorkerSpecificationConstants.OS_VENDOR, gatheringCore.getOSVendorName());
		putAttribute(WorkerSpecificationConstants.SYS_ARCHITECTURE, gatheringCore.getArchitecture());
		putAttribute(WorkerSpecificationConstants.OS_WORD_LENGTH, gatheringCore.getDataModel());
		putAttribute(WorkerSpecificationConstants.OS_UP_TIME, format(gatheringCore.getUpTime()));
	}
}
