/**
 * 
 */
package org.ourgrid.common.executor.vmware;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.executor.SandBoxEnvironment;
import org.ourgrid.common.executor.config.VMWareExecutorConfiguration;
import org.ourgrid.worker.WorkerConstants;

public class VMWareSandBoxEnvironmentTest {

	private String HOST_PLAYPEN;
	private File host_playpenDir;
	private String HOST_STORAGE_DIR;
	private File host_StorageDir;
	private Map<String, String> env;
	private VMWareExecutorConfiguration configuration;
	private VMWareSandBoxEnvironment vmWareEnv;
	private final String VPLAYPEN = System.getProperty("java.io.tmpdir");

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		String quotes = "\"";
		String vm_image_path = "[standard] debian/debian.vmx";
		
		String hostAdress = "https://127.0.0.1:8333/sdk";
		String hostUser = "sebastiao";
		String hostPasswd = "se143Basita";
		String guestUser = "root";
		String guestPasswd = "root";
		String playpenVM = "/tmp/vm/playpen";
		String storageVM = "/tmp/vm/storage";
		
		String systemTemp = System.getProperty("java.io.tmpdir");
		 
		HOST_PLAYPEN =  systemTemp + File.separator + "vplaypen";
		host_playpenDir = new File(HOST_PLAYPEN);
		host_playpenDir.mkdir();

		HOST_STORAGE_DIR = systemTemp + File.separator + "storage-host-dir";
		host_StorageDir = new File(HOST_STORAGE_DIR);
		host_StorageDir.mkdir();
		
		env = new HashMap<String, String>();
		env.put(WorkerConstants.ENV_PLAYPEN, host_playpenDir.getAbsolutePath());
		env.put(WorkerConstants.ENV_STORAGE, host_StorageDir.getAbsolutePath());
		
		configuration = new VMWareExecutorConfiguration(new File("."));
		
		Map<String, String> vm_props = new HashMap<String, String>();
		
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_IMAGE_PATH.toString(), vm_image_path);
		
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_HOST_ADDRESS.toString(), hostAdress);
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_HOST_USER.toString(), hostUser);
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_HOST_PASSWD.toString(), hostPasswd);
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_GUEST_USER.toString(), guestUser);
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_GUEST_PASSWD.toString(), guestPasswd);
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.PLAYPEN_DIR_IN_VM.toString(), playpenVM);
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.STORAGE_DIR_IN_VM.toString(), storageVM);
		
		configuration.loadCustomProperty(vm_props);
	}
	
	/**
	 * Creates an executor and does not invoke init command. The state machine do not allows 
	 * <code>executeRemoteCommand</code> invocation.
	 */
	@Test
	public void testEnvironmentConfiguration() throws ExecutorException{
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		try {
			vmWareEnv.executeRemoteCommand(VPLAYPEN, "echo a", new HashMap<String, String>());
			Assert.fail();
		} catch (ExecutorException e) { }
	}
	
	@Test
	public void testErrorOnInitScript() throws ExecutorException{
		
		Map<String, String> vm_props = new HashMap<String, String>();
		
		//The original configuration is modified here. The path of VM is set wrong. 
		vm_props.put(WorkerConstants.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_IMAGE_PATH.toString(), "wrong path to VM");
		
		configuration.loadCustomProperty(vm_props);
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		Map< String, String > envVars = new HashMap< String, String >(env);
		envVars.put( "USER", "thiagoepdc" );
		
		try {
			vmWareEnv.initSandboxEnvironment(envVars);
			vmWareEnv.prepareAllocation();
			Assert.fail();
		} catch (ExecutorException e) { }
	}
	
	@Test
	public void testCompleteExecution_timed_Output_No_Error() throws ExecutorException, InterruptedException{
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		ExecutorResult expectedResult = new ExecutorResult();
		expectedResult.setExitValue(0);
		expectedResult.setStdout("a");
		expectedResult.setStderr("");
		
		vmWareEnv.initSandboxEnvironment(env);
		vmWareEnv.prepareAllocation();
		
		vmWareEnv.executeRemoteCommand(VPLAYPEN , "echo a ; sleep 5", new HashMap< String, String >());
		
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		
		vmWareEnv.finishExecution();
		
		Assert.assertEquals(expectedResult.getExitValue(), result.getExitValue());
		Assert.assertEquals(expectedResult.getStdout().trim(), result.getStdout().trim());
		Assert.assertEquals(expectedResult.getStderr().trim(), result.getStderr().trim());
		
		vmWareEnv.shutDownSandBoxEnvironment();
	}
	
	@Test
	public void testCompleteExecution() throws ExecutorException, InterruptedException{
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		ExecutorResult expectedResult = new ExecutorResult();
		expectedResult.setExitValue(0);
		expectedResult.setStdout("a" + System.getProperty("line.separator") + "b");
		expectedResult.setStderr("");
		
		vmWareEnv.initSandboxEnvironment(env);
		vmWareEnv.prepareAllocation();
		
		vmWareEnv.executeRemoteCommand(VPLAYPEN , "echo a ; echo b", new HashMap< String, String >());
		
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		vmWareEnv.finishExecution();
		
		Assert.assertEquals(expectedResult.getExitValue(), result.getExitValue());
		Assert.assertEquals(expectedResult.getStdout().trim(), result.getStdout().trim());
		Assert.assertEquals(expectedResult.getStderr().trim(), result.getStderr().trim());
		
		vmWareEnv.shutDownSandBoxEnvironment();
	}
	
	
	@Test
	public void testErrorExecution() throws ExecutorException, InterruptedException{
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		ExecutorResult expectedResult = new ExecutorResult();
		expectedResult.setStdout("");
		
		Map< String, String > envVars = new HashMap< String, String >(env);
		envVars.put( "USER", "thiagoepdc" );
		
		int rInt = new Random().nextInt();
		
		vmWareEnv.initSandboxEnvironment(envVars);
		vmWareEnv.prepareAllocation();
		
		vmWareEnv.executeRemoteCommand(VPLAYPEN, "errorbinary a >> "+rInt+".txt", envVars);

		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		vmWareEnv.finishExecution();
		
		Assert.assertFalse(result.getExitValue() == 0);
		Assert.assertFalse(result.getStderr().trim().equals(""));
		Assert.assertEquals(expectedResult.getStdout().trim(), result.getStdout().trim());
		
		vmWareEnv.shutDownSandBoxEnvironment();
	}
	
	@Test
	public void testCompleteExecution_Neither_Output_Nor_Error() throws ExecutorException, InterruptedException{
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		ExecutorResult expectedResult = new ExecutorResult();
		expectedResult.setExitValue(0);
		expectedResult.setStdout("");
		expectedResult.setStderr("");
		
		Map< String, String > envVars = new HashMap< String, String >(env);
		envVars.put( "USER", "thiagoepdc" );
		
		int rInt = new Random().nextInt();
		
		vmWareEnv.initSandboxEnvironment(envVars);
		vmWareEnv.prepareAllocation();
		
		vmWareEnv.executeRemoteCommand(VPLAYPEN, "echo a >> "+rInt+".txt", envVars);
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		vmWareEnv.finishExecution();
		
		Assert.assertTrue(new File(HOST_PLAYPEN+File.separator+rInt+".txt").exists());
		Assert.assertEquals(expectedResult.getExitValue(), result.getExitValue());
		Assert.assertEquals(expectedResult.getStdout().trim(), result.getStdout().trim());
		Assert.assertEquals(expectedResult.getStderr().trim(), result.getStderr().trim());
		
		vmWareEnv.shutDownSandBoxEnvironment();
		
		//in the executions ends, the vm created files should be in the host playpen
		Assert.assertTrue(new File(HOST_PLAYPEN+File.separator+rInt+".txt").exists());
	}
	
	@Test
	public void testCompleteExecution_Create_OutputNoNameTest() throws ExecutorException, InterruptedException{
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		ExecutorResult expectedResult = new ExecutorResult();
		expectedResult.setExitValue(0);
		expectedResult.setStdout("b");
		expectedResult.setStderr("");
		
		Map< String, String > envVars = new HashMap< String, String >(env);
		envVars.put( "USER", "thiagoepdc" );
		
		int rInt = new Random().nextInt();
		
		vmWareEnv.initSandboxEnvironment(envVars);
		vmWareEnv.prepareAllocation();
		
		vmWareEnv.executeRemoteCommand(VPLAYPEN, "echo a >> "+rInt+".txt; echo b", envVars);
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		
		vmWareEnv.finishExecution();
		
		File file = new File(HOST_PLAYPEN+File.separator+rInt+".txt");
		Assert.assertTrue(file.exists());
		Assert.assertEquals(expectedResult.getExitValue(), result.getExitValue());
		Assert.assertEquals(expectedResult.getStdout().trim(), result.getStdout().trim());
		Assert.assertEquals(expectedResult.getStderr().trim(), result.getStderr().trim());
		
		vmWareEnv.shutDownSandBoxEnvironment();
		
		//in the executions ends, the vm created files should be in the host playpen
		Assert.assertTrue(new File(HOST_PLAYPEN+File.separator+rInt+".txt").exists());
	}
	
	@Test
	public void testExecutionWithPlaypenAndStorage() throws ExecutorException, IOException, InterruptedException {
		
		File play1 = new File(host_playpenDir.getAbsolutePath() + File.separator + "file1");
		File play2 = new File(host_playpenDir.getAbsolutePath() + File.separator + "file2");
		writeStringToFile(play1, "foo");
		writeStringToFile(play2, "bar");
		
		File stored1 = new File(host_StorageDir.getAbsolutePath() + File.separator + "file1");
		File stored2 = new File(host_StorageDir.getAbsolutePath() + File.separator + "file2");
		writeStringToFile(stored1, " our");
		writeStringToFile(stored2, "grid");
		
		env.put(WorkerConstants.STORED_FILES_ENV_VAR + "0", stored1.getAbsolutePath());
		env.put(WorkerConstants.STORED_FILES_ENV_VAR + "1", stored2.getAbsolutePath());
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		vmWareEnv.initSandboxEnvironment(env);
		vmWareEnv.prepareAllocation();
		
		vmWareEnv.executeRemoteCommand(VPLAYPEN, 
				"cat $PLAYPEN/file1 $PLAYPEN/file2 $STORAGE/file1 $STORAGE/file2 " +
				"&& mkdir -p $PLAYPEN/out/ && mkdir -p $STORAGE/out/ && touch $PLAYPEN/out/out && touch $STORAGE/out/out", env);
		
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		
		vmWareEnv.finishExecution();
		
		Assert.assertEquals("", result.getStderr().trim());
		Assert.assertEquals("foobar ourgrid", result.getStdout().trim());
		Assert.assertEquals(0, result.getExitValue());
		
		vmWareEnv.shutDownSandBoxEnvironment();
		
		//in the executions ends, the vm created files should be in the host playpen-storage
		Assert.assertTrue(new File(host_playpenDir.getAbsolutePath() + File.separator + "out" + File.separator + "out").exists());
		Assert.assertFalse(new File(host_StorageDir.getAbsolutePath() + File.separator + "out" + File.separator + "out").exists());
	}
	
	@Test
	public void testExecutionWithPlaypen() throws ExecutorException, IOException, InterruptedException {
		
		File play1 = new File(host_playpenDir.getAbsolutePath() + File.separator + "file1");
		File play2 = new File(host_playpenDir.getAbsolutePath() + File.separator + "file2");
		writeStringToFile(play1, "foo");
		writeStringToFile(play2, "bar");
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		vmWareEnv.initSandboxEnvironment(env);
		
		vmWareEnv.prepareAllocation();

		vmWareEnv.executeRemoteCommand(VPLAYPEN,
                "cat $PLAYPEN/file1 $PLAYPEN/file2 && mkdir -p $PLAYPEN/out/ && touch $PLAYPEN/out/out", env);
		
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		vmWareEnv.finishExecution();
		
		Assert.assertEquals("", result.getStderr().trim());
		Assert.assertEquals("foobar", result.getStdout().trim());
		Assert.assertEquals(0, result.getExitValue());
		
		vmWareEnv.shutDownSandBoxEnvironment();
		
		//in the executions ends, the vm created files should be in the host playpen
		Assert.assertTrue(new File(host_playpenDir.getAbsolutePath() + File.separator + "out" + File.separator + "out").exists());
	}
	
	@Test
	public void testExecutionScriptExecution() throws ExecutorException, IOException, InterruptedException {
		
		File play1 = new File(host_playpenDir.getAbsolutePath() + File.separator + "script.sh");
		writeStringToFile(play1, "echo foo");
		
		vmWareEnv = new VMWareSandBoxEnvironment(null);
		vmWareEnv.setConfiguration(configuration);
		
		vmWareEnv.initSandboxEnvironment(env);
		vmWareEnv.prepareAllocation();
		vmWareEnv.executeRemoteCommand(VPLAYPEN, "sh $PLAYPEN/script.sh", env);
		
		ExecutorResult result = waitAndGetExecutorResult(vmWareEnv);
		vmWareEnv.finishExecution();
		
		Assert.assertEquals("", result.getStderr().trim());
		Assert.assertEquals("foo", result.getStdout().trim());
		Assert.assertEquals(0, result.getExitValue());
		
		vmWareEnv.shutDownSandBoxEnvironment();
	}
	
	private void writeStringToFile(File file, String msg) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		writer.write(msg);
		writer.flush();
		writer.close();
	}
	
	private ExecutorResult waitAndGetExecutorResult(SandBoxEnvironment serverExec) throws ExecutorException,
																						InterruptedException {
		
		long timeout = 1000 * 10;
		long counter = 0;
		final int quantum = 100;
		
		while ( (!serverExec.hasExecutionFinished()) && (! (counter >= timeout))){
			Thread.sleep(quantum);
			counter += quantum;
		}
		
		return serverExec.getResult();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	
		//clean up
//        if(buildAndRunProcess(
//                parseCommand("sh "+ VSERVER_TEST_BASE_DIR+File.separator+"vserver_status "+VSERVER_GUEST), "")) {
//            
//    		assertTrue(buildAndRunProcess(
//                    parseCommand("sh "+ VSERVER_TEST_BASE_DIR+File.separator+"vserver_stopvm "+VSERVER_GUEST), ""));
//        }
		
		if(host_StorageDir.exists()) {
			deleteFile(host_StorageDir);
		}

		if(host_playpenDir.exists()) {
			for (File file : host_playpenDir.listFiles()) {
				deleteFile(file);
			}
		}

	}

	private void deleteFile(File file) {
		if(file.isDirectory()){
			for (File f : file.listFiles()) {
				deleteFile(f);
			}
		}
		file.delete();
	}
}