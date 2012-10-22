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
package org.ourgrid.broker.controlws;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.management.BrokerManager;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.specification.exception.JobSpecificationException;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.specification.peer.PeerSpecification;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;


public class BrokerControlWSFacade extends SyncApplicationClient<BrokerManager, BrokerControlWSManagerClient> {

	
	private static final String WS_FILES_DIR = "wsfiles";
	
	private static final String FILE_SEP = System.getProperty("file.separator");
	
	public BrokerControlWSFacade(ModuleContext context) throws CommuneNetworkException,
		ProcessorStartException {
				
		super("BROKER_SYNC_WEB_SERVICE", context);
	}
	
	@Override
	protected InitializationContext<BrokerManager, BrokerControlWSManagerClient> createInitializationContext() {
		return new BrokerControlWSInitializationContext();
	}
	
	public ControlOperationResult addJob( WSJobSpec job ) {
		
		setJobFilesDir(job);
		
		getManager().addJob(getManagerClient(), convertJobSpec(job));
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class);
	}

	public ControlOperationResult cancelJob( int jobID ) {
		getManager().cancelJob(getManagerClient(), jobID);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class);
	}

	public ControlOperationResult cleanAllFinishedJobs() {
		getManager().cleanAllFinishedJobs(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class);

	}

	public ControlOperationResult cleanFinishedJob( int jobID ) {
		getManager().cleanFinishedJob(getManagerClient(), jobID);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class);
	}

	public BrokerCompleteStatus getBrokerCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, BrokerCompleteStatus.class);
	}
	
	public byte[] getFile(String fileName) {
		
		File file = new File(WS_FILES_DIR + FILE_SEP + fileName);
		
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException fe) {
			return new byte[0];
		}
        
        
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
        	return new byte[0];
        }
    
        byte[] bytes = new byte[(int)length];
    
        int offset = 0;
        int numRead = 0;
        
        try {
	        while (offset < bytes.length
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	        if (offset < bytes.length) {
	            throw new IOException("N�o foi poss�vel ler o arquivo "+file.getName());
	        }
	    
	        is.close();
        } catch (IOException ioe) {
        	return new byte[0];
        }
        
        return bytes;
	}   
	
	public void sendFile(byte[] file, String fileName) {
		
		File ws = new File(WS_FILES_DIR);
		
	    try{  
	    	
	    	if (!ws.exists()) {
	    		ws.mkdir();
	    	}
	    	
	        FileOutputStream fos = new FileOutputStream(WS_FILES_DIR + FILE_SEP + fileName);  
	        fos.write(file);  
	        FileDescriptor fd = fos.getFD();  
	        fos.flush();  
	        fd.sync();  
	        fos.close();   
	    }  
	   catch(Exception e){  
		   e.printStackTrace();
	   }  
	}
	
	private void setJobFilesDir(WSJobSpec job) {
		
		File ws = new File(WS_FILES_DIR);
		
    	if (!ws.exists()) {
    		ws.mkdir();
    	}
		
		List<WSTaskSpec> taskSpecs = job.getTaskSpecs();
		
		if (taskSpecs != null) {
			
			List<WSIOEntry> initBlock = null;
			String srcFile = null;
			
			List<WSIOEntry> finalBlock = null;
			String destFile = null;
			
			for (WSTaskSpec task : taskSpecs) {
				
				initBlock = task.getInitBlock();

				if (initBlock != null) {
					for (WSIOEntry ioEntry : initBlock) {
						srcFile = ioEntry.getSourceFile();
						ioEntry.setSourceFile(ws.getAbsolutePath() + FILE_SEP + srcFile);
					}
				}
				
				finalBlock = task.getFinalBlock();
				
				if (finalBlock != null) {
					for (WSIOEntry ioEntry : finalBlock) {
						destFile = ioEntry.getDestination();
						ioEntry.setDestination(ws.getAbsolutePath() + FILE_SEP + destFile);
					}
				}
			}
		}	
	}
	
	private JobSpecification convertJobSpec(WSJobSpec wsSpec) {
		
		JobSpecification spec = new JobSpecification();
		spec.setLabel(wsSpec.getLabel());
		spec.setRequirements(wsSpec.getRequirements());

		List<WSTaskSpec> wsTasks = wsSpec.getTaskSpecs();
		List<TaskSpecification> tasks = new ArrayList<TaskSpecification>();
		
		if (wsTasks != null) {
			
			TaskSpecification taskSpec = null;
			IOBlock initBlock = null;
			IOBlock finalBlock = null;
			
			for (WSTaskSpec wsTask : wsTasks) {
				
				taskSpec = new TaskSpecification();
				taskSpec.setRemoteExec(wsTask.getRemoteExec());
				taskSpec.setSabotageCheck(wsTask.getSabotageCheck());
				taskSpec.setSourceDirPath(wsTask.getSourceParentDir());
				taskSpec.setTaskSequenceNumber(wsTask.getTaskSequenceNumber());
				
				initBlock = new IOBlock();
				List<WSIOEntry> wsInitBlock = wsTask.getInitBlock();
				if (wsInitBlock != null) {
					
					for (WSIOEntry wsEntry : wsInitBlock) {
						initBlock.putEntry(new IOEntry(wsEntry.getCommand(), wsEntry.getSourceFile(),
								wsEntry.getDestination()));
					}
					
					taskSpec.setInitBlock(initBlock);
				}
				
				finalBlock = new IOBlock();
				List<WSIOEntry> wsFinalBlock = wsTask.getFinalBlock();
				if (wsFinalBlock != null) {
					
					for (WSIOEntry wsEntry : wsFinalBlock) {
						finalBlock.putEntry(new IOEntry(wsEntry.getCommand(), wsEntry.getSourceFile(),
								wsEntry.getDestination()));
					}
					
					taskSpec.setFinalBlock(finalBlock);
				}
				
				tasks.add(taskSpec);
			}
		}
		
		try {
			spec.setTaskSpecs(tasks);
		} catch (JobSpecificationException e) {
			e.printStackTrace();
		}
		
		return spec;
	}
		
}
