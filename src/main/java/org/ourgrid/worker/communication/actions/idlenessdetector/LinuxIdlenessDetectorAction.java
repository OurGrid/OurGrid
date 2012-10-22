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
package org.ourgrid.worker.communication.actions.idlenessdetector;

import java.io.Serializable;

import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.request.LinuxIdlenessDetectorActionRequestTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

/**
 */
public class LinuxIdlenessDetectorAction implements RepeatedAction {
	
	private static final String AMD64 = "amd64";
	private static final String I386 = "i386";
	

	public void run(Serializable handler, ServiceManager serviceManager) {
		LinuxIdlenessDetectorActionRequestTO to = new LinuxIdlenessDetectorActionRequestTO();
		String osType= System.getProperty("os.arch");
		
		if (osType.equals(AMD64)) {
			to.setXIdleTimeLibPath(serviceManager.getContainerContext().getProperty(WorkerConfiguration.PROP_XIDLETIME64_PATH));
		} else if (osType.equals(I386)) {
			to.setXIdleTimeLibPath(serviceManager.getContainerContext().getProperty(WorkerConfiguration.PROP_XIDLETIME32_PATH));
		}
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
