package org.ourgrid.peer.status.util;

import java.lang.reflect.Method;
import java.util.List;

import org.ourgrid.common.statistics.beans.aggregator.AG_Attribute;
import org.ourgrid.common.statistics.beans.aggregator.AG_Command;
import org.ourgrid.common.statistics.beans.aggregator.AG_GridProcess;
import org.ourgrid.common.statistics.beans.aggregator.AG_Job;
import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_Task;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_PeerStatusChange;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.common.statistics.beans.peer.Attribute;
import org.ourgrid.common.statistics.beans.peer.Command;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.beans.peer.Login;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.Task;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.beans.peer.monitor.WorkerStatusChange;

public class PeerHistoryStatusBuilderHelper {
	
	public static AG_WorkerStatusChange convertWorkerStatusChange(WorkerStatusChange wsc){
		AG_WorkerStatusChange workerChange = new AG_WorkerStatusChange();
		
		workerChange.setLastModified(wsc.getLastModified());
		workerChange.setStatus(wsc.getStatus());
		workerChange.setTimeOfChange(wsc.getTimeOfChange());
		
		return workerChange;
	}
	
	public static AG_Peer convertPeerSimple(Peer dbPeer) {
		AG_Peer peer = new AG_Peer();
		peer.setAddress(dbPeer.getAddress());
		peer.setDescription(dbPeer.getDescription());
		peer.setEmail(dbPeer.getEmail());
		peer.setLabel(dbPeer.getLabel());
		peer.setLastModified(dbPeer.getLastModified());
		peer.setLatitude(dbPeer.getLatitude());
		peer.setLongitude(dbPeer.getLongitude());
		peer.setTimezone(dbPeer.getTimezone());
		peer.setVersion(dbPeer.getVersion());
		
		return peer;
	}
	
	
	public static AG_Peer convertPeer(Peer dbPeer) {
		AG_Peer peer = convertPeerSimple(dbPeer);
		return peer;
	}
		
	/**
	 * @param peer
	 */
	public static AG_Worker convertWorker(Worker w) {

		AG_Worker worker = new AG_Worker();
		worker.setAddress(w.getAddress());
		worker.setBeginTime(w.getBeginTime());
		worker.setEndTime(w.getEndTime());
		worker.setLastModified(w.getLastModified());
		worker.setStatus(w.getStatus());
		worker.setAllocatedFor(w.getAllocatedFor());
		return worker;
	}

	public static AG_Attribute convertAttribute(Attribute att) {
		AG_Attribute attribute = new AG_Attribute();

		attribute.setBeginTime(att.getBeginTime());
		attribute.setEndTime(att.getEndTime());
		attribute.setLastModified(att.getLastModified());
		attribute.setProperty(att.getProperty());
		attribute.setValue(att.getValue());
		return attribute;
	}
	
	/**
	 * @param peer
	 */
	public static AG_User convertUser(User u) {
		
		AG_User user = new AG_User();
		user.setAddress(u.getAddress());
		user.setCreationDate(u.getCreationDate());
		user.setDeletionDate(u.getDeletionDate());
		user.setLastModified(u.getLastModified());
		user.setPublicKey(u.getPublicKey());

		return user;
		
	}

	/**
	 * @param peer 
	 * @param user
	 */
	public static AG_Login convertLogin(Login l) {
		AG_Login login = new AG_Login();
		login.setBeginTime(l.getBeginTime());
		login.setEndTime(l.getEndTime());
		login.setLastModified(l.getLastModified());
		login.setLoginResult(l.getLoginResult());

		return login;
	}

	public static AG_Job convertJob(Job j) {

		AG_Job job = new AG_Job();
		job.setCreationTime(j.getCreationTime());
		job.setFinishTime(j.getFinishTime());
		job.setJobId(j.getJobId());
		job.setLabel(j.getLabel());
		job.setLastModified(j.getLastModified());
		job.setMaxFails(j.getMaxFails());
		job.setMaxReplicas(j.getMaxReplicas());
		job.setRequestId(j.getRequestId());
		job.setRequirements(j.getRequirements());
		job.setStatus(j.getStatus());

		return job;
	}

	public static AG_Task convertTask(Task t) {

		AG_Task task = new AG_Task();
		task.setActualFails(t.getActualFails());
		task.setLastModified(t.getLastModified());
		task.setRemoteExec(t.getRemoteExec());
		task.setSabotageCheck(t.getSabotageCheck());
		task.setStatus(t.getStatus());
		task.setSequenceNumber(t.getSequenceNumber());

		return task;
	}

	public static AG_GridProcess convertProcess(GridProcess p) {

		AG_GridProcess process = new AG_GridProcess();

		process.setCreationTime(p.getCreationTime());
		process.setErrorCause(p.getErrorCause());
		process.setExecutionErrorType(p.getExecutionErrorType());
		process.setExitValue(p.getExitValue());
		process.setFinalBeginning(p.getFinalBeginning());
		process.setFinalEnd(p.getFinalEnd());
		process.setInitBeginning(p.getInitBeginning());
		process.setInitEnd(p.getInitEnd());
		process.setLastModified(p.getLastModified());
		process.setLatestPhase(p.getLatestPhase());
		process.setRemoteBeginning(p.getRemoteBeginning());
		process.setRemoteEnd(p.getRemoteEnd());
		process.setSabotageCheck(p.getSabotageCheck());
		process.setStatus(p.getStatus());
		process.setSequenceNumber(p.getSequenceNumber());
		process.setWorkerAddress(p.getWorkerAddress());
		process.setStderr(p.getStderr());
		process.setStdout(p.getStdout());
		
		return process;
	}

	public static AG_Command convertCommand(Command c) {
		AG_Command command = new AG_Command();

		command.setDestination(c.getDestination());
		command.setFileName(c.getFileName());
		command.setFileSize(c.getFileSize());
		command.setLastModified(c.getLastModified());
		command.setName(c.getName());
		command.setProgress(c.getProgress());
		command.setSource(c.getSource());
		command.setStatus(c.getStatus());
		command.setTransferBegin(c.getTransferBegin());
		command.setTransferEnd(c.getTransferEnd());
		command.setTransferRate(c.getTransferRate());
		
		return command;
	}


	public static void simplePeerCopy(AG_Peer peer, AG_Peer dbPeer) {
		dbPeer.setDescription(peer.getDescription());
		dbPeer.setEmail(peer.getEmail());
		dbPeer.setLabel(peer.getLabel());
		dbPeer.setLastModified(peer.getLastModified());
		dbPeer.setLatitude(peer.getLatitude());
		dbPeer.setLongitude(peer.getLongitude());
		dbPeer.setTimezone(peer.getTimezone());
		dbPeer.setVersion(peer.getVersion());
	}


	public static void simpleUserCopy(AG_User user, AG_User dbUser) {
		dbUser.setCreationDate(user.getCreationDate());
		dbUser.setDeletionDate(user.getDeletionDate());
		dbUser.setLastModified(user.getLastModified());
		dbUser.setPassword(user.getPassword());
		dbUser.setPublicKey(user.getPublicKey());
	}


	public static void simpleWorkerCopy(AG_Worker worker, AG_Worker dbWorker) {
		dbWorker.setBeginTime(worker.getBeginTime());
		dbWorker.setEndTime(worker.getEndTime());
		dbWorker.setLastModified(worker.getLastModified());
		dbWorker.setStatus(worker.getStatus());
		dbWorker.setAllocatedFor(worker.getAllocatedFor());
	}


	public static void simpleLoginCopy(AG_Login login, AG_Login dbLogin) {
		dbLogin.setBeginTime(login.getBeginTime());
		dbLogin.setEndTime(login.getEndTime());
		dbLogin.setLastModified(login.getLastModified());
		dbLogin.setLoginResult(login.getLoginResult());
	}


	public static void simpleJobCopy(AG_Job job, AG_Job dbJob) {
		dbJob.setCreationTime(job.getCreationTime());
		dbJob.setFinishTime(job.getFinishTime());
		dbJob.setJobId(job.getJobId());
		dbJob.setLabel(job.getLabel());
		dbJob.setLastModified(job.getLastModified());
		dbJob.setMaxFails(job.getMaxFails());
		dbJob.setMaxReplicas(job.getMaxReplicas());
		dbJob.setRequestId(job.getRequestId());
		dbJob.setRequirements(job.getRequirements());
		dbJob.setStatus(job.getStatus());
	}


	public static void simpleTaskCopy(AG_Task task, AG_Task dbTask) {
		dbTask.setActualFails(task.getActualFails());
		dbTask.setLastModified(task.getLastModified());
		dbTask.setRemoteExec(task.getRemoteExec());
		dbTask.setSabotageCheck(task.getSabotageCheck());
		dbTask.setSequenceNumber(task.getSequenceNumber());
		dbTask.setStatus(task.getStatus());
	}


	public static void simpleProcessCopy(AG_GridProcess process, AG_GridProcess dbProcess) {
		dbProcess.setCreationTime(process.getCreationTime());
		dbProcess.setErrorCause(process.getErrorCause());
		dbProcess.setExecutionErrorType(process.getExecutionErrorType());
		dbProcess.setExitValue(process.getExitValue());
		dbProcess.setFinalBeginning(process.getFinalBeginning());
		dbProcess.setFinalEnd(process.getFinalEnd());
		dbProcess.setInitBeginning(process.getInitBeginning());
		dbProcess.setInitEnd(process.getInitEnd());
		dbProcess.setLastModified(process.getLastModified());
		dbProcess.setLatestPhase(process.getLatestPhase());
		dbProcess.setRemoteBeginning(process.getRemoteBeginning());
		dbProcess.setRemoteEnd(process.getRemoteEnd());
		dbProcess.setStatus(process.getStatus());
		dbProcess.setSequenceNumber(process.getSequenceNumber());
		dbProcess.setWorkerAddress(process.getWorkerAddress());
		dbProcess.setSabotageCheck(process.getSabotageCheck());
		dbProcess.setStderr(process.getStderr());
		dbProcess.setStdout(process.getStdout());
	}


	public static void simpleCommandCopy(AG_Command cmd, AG_Command dbCommand) {
		dbCommand.setDestination(cmd.getDestination());
		dbCommand.setFileName(cmd.getFileName());
		dbCommand.setFileSize(cmd.getFileSize());
		dbCommand.setLastModified(cmd.getLastModified());
		dbCommand.setName(cmd.getName());
		dbCommand.setProgress(cmd.getProgress());
		dbCommand.setSource(cmd.getSource());
		dbCommand.setStatus(cmd.getStatus());
		dbCommand.setTransferBegin(cmd.getTransferBegin());
		dbCommand.setTransferEnd(cmd.getTransferEnd());
		dbCommand.setTransferRate(cmd.getTransferRate());
	}
	
	public static <T> boolean equals(T t, T t2, List<String> excludedProperties){
		Class<? extends Object> clazz = t.getClass();
		Class<? extends Object> clazz2 = t2.getClass();
		
		if(!clazz.equals(clazz2)){
			return false;
		}
		
		String TPackageName = clazz.getPackage().getName();
		
		for(Method method : clazz.getMethods()){
			String methodName = method.getName();

			boolean isAGet = methodName.startsWith("get");
			if(isAGet){
				Class<?> returnType = method.getReturnType();
				String methodReturnPackage = (returnType.isPrimitive() ? returnType.getName() : returnType.getPackage().getName());
				String firstLetter = methodName.substring(3,4).toLowerCase();
				String theRestOfTheName = methodName.substring(4);
				
				String propertieName = firstLetter+theRestOfTheName;
				boolean notExcludedPropertie = !excludedProperties.contains(propertieName);
				boolean notACollection = !returnType.isAssignableFrom(List.class);
				boolean notInTheSamePackage = !methodReturnPackage.equals(TPackageName);
				
				if(notExcludedPropertie && notACollection && notInTheSamePackage){
					try {
						Object result = method.invoke(t, (Object[])null);
						Object result2 = method.invoke(t2, (Object[])null);

						if(result != null && result2 != null){
							if(!result.equals(result2)){
								return false;
							}
						}
						else{
							if((result != null && result2 == null) || 
							   (result == null && result2 != null)){

								return false;
							}
						}
					} catch (Exception e) {
						return false;
					}
				}
			}
		}
		return true;
	}


	public static void simpleWorkerStatusChangeCopy(AG_WorkerStatusChange wsc, AG_WorkerStatusChange dbChange) {
		dbChange.setLastModified(wsc.getLastModified());
		dbChange.setStatus(wsc.getStatus());
		dbChange.setTimeOfChange(wsc.getTimeOfChange());
	}


	public static AG_PeerStatusChange convertPeerStatusChange(DS_PeerStatusChange psc) {
		AG_PeerStatusChange agPsc = new AG_PeerStatusChange();
		
		agPsc.setCurrentStatus(psc.getCurrentStatus());
		agPsc.setLastModified(psc.getLastModified());
		agPsc.setTimeOfChange(psc.getTimeOfChange());
		agPsc.setVersion(psc.getVersion());
		
		return agPsc;
		
	}
}
