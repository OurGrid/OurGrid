/*
 * Copyright (C) 2009 Universidade Federal de Campina Grande
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
package org.ourgrid.worker.sysmonitor.core;

import java.util.LinkedList;
import java.util.List;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.pager.PageControl;
import org.hyperic.sigar.shell.ShellCommandBase;

/**
 *
 */
public class SysInfoGatheringCore extends ShellCommandBase {

	protected Shell shell;
	protected Sigar sigar;
	protected SigarProxy sigarProxy;

	private CpuInfo cpuInfo;
	private OperatingSystem operatingSystem;
	private Mem primaryMemory;
	private CpuPerc[] cpuPercList;
	private FileSystemUsage fileSystemUsage;
	private String installationPartition;

	private long workerPid;

	protected SysInfoGatheringCore(Shell shell) {
		this.shell = shell;
		this.sigar = shell.getSigar();
		this.sigarProxy = shell.getSigarProxy();
		this.operatingSystem = OperatingSystem.getInstance();
		this.workerPid = sigar.getPid();
		try{
			this.cpuInfo = sigar.getCpuInfoList()[0];
			this.primaryMemory = sigar.getMem();
			this.installationPartition = getInstallationPartitionName();
			this.cpuPercList = sigar.getCpuPercList();
			this.fileSystemUsage = sigar.getFileSystemUsage(installationPartition);
		}catch (SigarException e) {
			this.cpuInfo = null;
			this.primaryMemory = null;
			this.installationPartition = null;
			this.cpuPercList = null;
			this.fileSystemUsage = null;
		}
	}

	/**
	 * 
	 */
	public SysInfoGatheringCore() {
		this(new Shell());
		this.shell.setPageSize(PageControl.SIZE_UNLIMITED);
	}

	/**
	 * This method should be used to update dynamic informations about cpu, 
	 * disk usage and memory usage.
	 */
	public void gather() {
		try {
			this.cpuPercList = sigar.getCpuPercList();
			this.fileSystemUsage = sigar.getFileSystemUsage(installationPartition);
			this.primaryMemory = sigar.getMem();
		} catch (SigarException e) {
			this.cpuPercList = null;
			this.fileSystemUsage = null;
			this.primaryMemory = null;			
		}
	}

	/**
	 * Return the CPU MHz.
	 * 
	 * @return MHz of CPU
	 */
	public int getCpuMhz() {
		return (this.cpuInfo == null )? -1: this.cpuInfo.getMhz();
	}

	/**
	 * CPU Model.
	 * @return
	 */
	public String getCpuModel() {
		return (this.cpuInfo == null)? null : cpuInfo.getModel();
	}

	/**
	 * Number of CPU cores. For example, if there are two physical CPUs and each one with 2 cores, this method
	 * returns 4.
	 * @return
	 */
	public int getCpuTotalCores() {
		return (this.cpuInfo == null)? -1 : this.cpuInfo.getTotalCores();
	}

	/**
	 * Name of the CPU vendor.
	 * 
	 * @return
	 */
	public String getCpuVendor() {
		return (this.cpuInfo == null)? null : this.cpuInfo.getVendor();
	}

	/**
	 * Partition name where the system is installed. 
	 * For example C:\ or D:\ for Windows or /dev1 for Linux.
	 * 
	 * @return
	 */
	public String getInstallationPartitionName() {
		String partitionName = null;
		try{
			long pid = sigar.getPid();
			String installationDir = sigarProxy.getProcExe(pid).getCwd();
			partitionName = sigarProxy.getFileSystemMap().
			getMountPoint(installationDir).getDirName();
		}catch(SigarException s){
		}
		return partitionName;
	}

	/**
	 * Retrieves the partition file system name. These values may be "NTFS", "ext3", etc. 
	 * 
	 * @return file system name
	 * 
	 */
	public String getFileSystemType() {
		String fileSystem = null;
		try{
			fileSystem =  sigarProxy.getFileSystemMap().
			getFileSystem(this.installationPartition).getSysTypeName();
		}catch(SigarException s){
		}
		return fileSystem;
	}

	/**
	 * Retrieves the partition total amount in KB where this process is running.
	 * 
	 * @return
	 *  
	 */
	public long getDiskTotal() {
		long diskTotal;
		try {
			diskTotal = this.sigarProxy.getFileSystemUsage(installationPartition).getTotal();
		} catch (SigarException e) {
			diskTotal = -1l;
		}
		return diskTotal;
	}

	/**
	 * Retrieves the total amount of primary memory in MBytes. 
	 * 
	 * @return the total amount.
	 * 
	 */
	public long getMemTotal() {
		long memTotal;
		try {
			memTotal = this.sigar.getMem().getRam();
		} catch (SigarException e) {
			memTotal = -1;
		}
		return memTotal;
	}

	/**
	 * OS name.
	 * @return
	 */
	public String getOSName() {
		return this.operatingSystem.getName();
	}

	/**
	 * OS description.
	 * @return
	 */
	public String getOSDescription() {
		return this.operatingSystem.getDescription();
	}

	/**
	 * Return the system architecture.
	 * @return
	 */
	public String getArchitecture(){
		return this.operatingSystem.getArch();
	}

	/**
	 * OS name vendor.
	 * @return
	 */
	public String getOSVendorName() {
		return this.operatingSystem.getVendor();
	}

	/**
	 * Retrieves the word length used by operating system. 
	 * @return either 32 or 64
	 */
	public String getDataModel() {
		return this.operatingSystem.getDataModel();
	}

	/**
	 * Retrieves how long the system has been run in seconds.
	 * 
	 */
	public double getUpTime() {
		double upTime;
		try {
			upTime = this.sigar.getUptime().getUptime();
		} catch (SigarException e) {
			upTime = -1.0;
		}
		return upTime;
	}


	/*Dynamic Informations*/

	/**
	 * Return a double array with idle time for each core.
	 * @return idle time array
	 *  
	 */
	public double[] getCpuIdle() {
		if(cpuPercList == null) return null;
		double[] idleTimes = new double[cpuPercList.length];
		for (int i = 0; i < idleTimes.length; i++) {
			idleTimes[i] = cpuPercList[i].getIdle();
		}
		return idleTimes;
	}

	/**
	 * Return a double array with user time for each core.
	 * 
	 * @return user time array
	 */
	public double[] getCpuUser() {
		if(cpuPercList == null) return null;
		double[] idleTimes = new double[cpuPercList.length];
		for (int i = 0; i < idleTimes.length; i++) {
			idleTimes[i] = cpuPercList[i].getUser();
		}
		return idleTimes;
	}

	/**
	 * Return a double array with system time for each core.
	 * <p>System time � CPU time used by running processes whose 
	 * owner is the system, not an user.
	 * 
	 * @return system time array
	 */
	public double[] getCpuSys() {
		if(cpuPercList == null) return null;
		double[] idleTimes = new double[cpuPercList.length];
		for (int i = 0; i < idleTimes.length; i++) {
			idleTimes[i] = cpuPercList[i].getSys();
		}
		return idleTimes;
	}

	/**
	 * Return a double array with nice time for each core. 
	 * <p>Nice time � CPU time used by processes running with 
	 * an altered scheduling priority.
	 * 
	 * @return nice time array
	 */
	public double[] getCpuNice() {
		if(cpuPercList == null) return null;
		double[] idleTimes = new double[cpuPercList.length];
		for (int i = 0; i < idleTimes.length; i++) {
			idleTimes[i] = cpuPercList[i].getNice();
		}
		return idleTimes;
	}

	/**
	 * Return a double array with wait time for each core. 
	 * <p>Wait time � CPU time used by processes which are waiting
	 * for input/output operations.
	 * 
	 * @return nice time array
	 */    
	public double[] getCpuWait() {
		if(cpuPercList == null) return null;
		double[] idleTimes = new double[cpuPercList.length];
		for (int i = 0; i < idleTimes.length; i++) {
			idleTimes[i] = cpuPercList[i].getWait();
		}
		return idleTimes;
	}

	/**
	 * Retrieves the total cpu usage of the system for each core. 
	 * 
	 * @return at each array position, the sum: wait + sys + user + nice
	 */
	public double[] getCpuCombined() {
		if(cpuPercList == null) return null;
		double[] idleTimes = new double[cpuPercList.length];
		for (int i = 0; i < idleTimes.length; i++) {
			idleTimes[i] = cpuPercList[i].getCombined();
		}
		return idleTimes;
	}

	/**
	 * Return the total percentage of all CPU's, ignoring the Job's process.
	 * If there is a single core, the max value is 1.0, if there are two cores,
	 * the max value is 2.0 and so on. 
	 * 
	 * @return
	 * @throws SigarException 
	 */
	public double getCpuPercSystemOnly() {
		double total = 0.0;
		for(double d: this.getCpuCombined()){
			total += d;
		}
		return total - this.getJobCpuPerc();	
	}

	/*
	 * It uses a BFS algorithm to search throw all process to
	 * identify which is child of the Worker process or grandson 
	 * and so on.
	 * 
	 */
	private double getJobCpuPerc() {
		double jobPerc = 0.0;
		long[] pids = null;
		try {
			pids = sigar.getProcList();
		} catch (SigarException e) {
			return -1.0;
		}
		
		//VM usage (sand-box execution)
		String pidExecFileName;
		for(int i = 0; i < pids.length; i++){
			try {
				pidExecFileName = sigar.getProcExe(pids[i]).getName();
			} catch (SigarException e) {
				pidExecFileName = "";
			}
			if(pidExecFileName.contains("vmware-vmx.exe")){
				try {
					jobPerc += sigar.getProcCpu(pids[i]).getPercent();
				} catch (SigarException e) {}
			}
		}
		
		//Worker process usage (javaw)
		try {
			jobPerc += sigar.getProcCpu(workerPid).getPercent();
		} catch (SigarException e1) {}
		
		//Worker children usage (vanilla execution)
		List<Long> ppids = new LinkedList<Long>();
		ppids.add(workerPid);
		long nextPpid;
		
		while(ppids.size() > 0){
			long ppid = ppids.remove(0);
			for(int i = 0; i < pids.length; i++){
				try {
					nextPpid = sigar.getProcState(pids[i]).getPpid();
				} catch (SigarException e) {
					nextPpid = 0;
				}
				if(nextPpid == ppid) {
					try {
						jobPerc += sigar.getProcCpu(pids[i]).getPercent();
						ppids.add(pids[i]);
					} catch (SigarException e) {}
				}
			}
		}
		return jobPerc;
	}
	
	/**
	 * Returns the amount of disk avail in Kb.
	 * 
	 * @return
	 */
	public long getDiskAvail() {
		return (this.fileSystemUsage == null)? -1 :this.fileSystemUsage.getAvail();
	}

	/**
	 * Return a triplet for the load average. Only Unix.
	 * @return 
	 */
	public double[] getLoadAvarage() {
		try {
			return this.sigar.getLoadAverage();
		} catch (SigarException e) {
			return null;
		}
		
	}

	/**
	 * Amount of free primary memory in Kb. 
	 * @return
	 * 
	 */
	public long getMemFree() {
		return (this.primaryMemory == null)? -1 : this.primaryMemory.getActualFree();
	}

	/**
	 * Percentage of free primary memory. 
	 * @return
	 * 
	 */
	public double getMemFreePercent() {
		return (this.primaryMemory == null)? -1 : this.primaryMemory.getFreePercent();
	}

	/**
	 * Load Sigar binaries.
	 */
	public static void loadLibraries() {
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ";lib");
	}
}
