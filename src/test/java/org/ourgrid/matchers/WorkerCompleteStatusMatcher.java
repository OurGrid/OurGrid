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
package org.ourgrid.matchers;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.worker.WorkerConfiguration;

import br.edu.ufcg.lsd.commune.context.ModuleContext;


public class WorkerCompleteStatusMatcher implements IArgumentMatcher {

	private final ModuleContext baseConfig;
	private final String peerUserAtServer;
	private final WorkerStatus workerStatus;
	private final String playPenPath;
	private final String storagePath;
	private final long initialTick;
	
	private final static long TIME_TOLERANCE = 15;
	
	private WorkerCompleteStatusMatcher(ModuleContext context, String peerUserAtServer,
			WorkerStatus status, String playpenDirPath, String storageDirPath, long tick) {
		this.baseConfig = context;
		this.peerUserAtServer = peerUserAtServer;
		this.workerStatus = status;
		this.playPenPath = playpenDirPath;
		this.initialTick = tick;
		this.storagePath = storageDirPath;
	}
	
	/**
	 * Matches WorkerCompleteStatus's configurations and if up time <= (finalTick - initialTick).
	 * finalTick is marked in this method.
	 * 
	 */
	public boolean matches(Object arg0) {
		
		long finalTick = System.currentTimeMillis();

		WorkerCompleteStatus completeStatus = ((WorkerCompleteStatus)arg0); 
		
		if(baseConfig == null) {
			if(completeStatus.getConfiguration() != null) {
				return false;
			}
		} else {
			if (!WorkerConfiguration.toString(baseConfig).equals(completeStatus.getConfiguration())) {
				return false;
			}
		}
		
		if (((completeStatus.getUpTime()) > (TIME_TOLERANCE + finalTick - initialTick))) {
			return false;
		}
		
		if(workerStatus == null) {
			if(completeStatus.getStatus() != null) {
				return false;
			}
		} else {
			if (!workerStatus.equals(completeStatus.getStatus())) {
				return false;
			}
		}
		
		if (peerUserAtServer == null && completeStatus.getPeerInfo().getPeerUserAtServer() != null) {
			return false;
		}
		
		if (peerUserAtServer != null && !peerUserAtServer.equals(completeStatus.getPeerInfo().getPeerUserAtServer())) {
			return false;
		}
		
		if(playPenPath == null) {
			if(completeStatus.getCurrentPlaypenDirPath() != null) {
				return false;
			}
		} else {
			String playpenPathOnSystem = completeStatus.getCurrentPlaypenDirPath();

			if(playpenPathOnSystem == null) {
				return false;
			}
			
			if(playpenPathOnSystem.indexOf(playPenPath) < 0){
				return false;
			}
			
			if(!playpenPathOnSystem.contains((this.playPenPath))){
				return false;
			}
//			Pattern pattern = Pattern.compile(".*.worker-.+");
//			Matcher matcher = pattern.matcher(playPenPath);
//			Matcher matcher2 = pattern.matcher(playpenPathOnSystem);
//			
//			String playpenRoot1 = playPenPath.substring(0,
//					playPenPath.lastIndexOf(System.getProperty("file.separator")));
//			String playpenRoot2 = playpenPathOnSystem.substring(0,
//					playpenPathOnSystem.lastIndexOf(System.getProperty("file.separator")));
//	
//			if (!matcher.matches() && !matcher2.matches() && !playpenRoot1.equals(playpenRoot2)) {
//				return false;
//			}
			
			
		}
		
		if(storagePath == null) {
			if(completeStatus.getCurrentStorageDirPath() != null) {
				return false;
			}
		} else {
			String currentStorageDirPath = completeStatus.getCurrentStorageDirPath();
			if (currentStorageDirPath.indexOf(storagePath) < 0) {
				return false;
			}
		}
		
		return true;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public static WorkerCompleteStatus eqMatcher(ModuleContext context, String peerUserAtServer,
			WorkerStatus status, String playpenDirPath, String storageDirPath, long tick) {
		EasyMock.reportMatcher(new WorkerCompleteStatusMatcher(context, peerUserAtServer, status,
				playpenDirPath, storageDirPath, tick));
		return null;
	}
	
}
