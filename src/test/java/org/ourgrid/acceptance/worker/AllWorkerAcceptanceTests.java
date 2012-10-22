package org.ourgrid.acceptance.worker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)  
@SuiteClasses({  
              Test_301_WorkerCreated.class,
              Test_302_WorkerOwner.class,
              Test_303_WorkerPreparing.class,
              Test_304_WorkerOwnerWithPeer.class,
              Test_305_WorkerPreparingWithPeer.class,
              Test_306_WorkerIdle.class,
              Test_307_WorkerError.class,
              Test_308_WorkerOwnerLoggedPeer.class,
              Test_309_WorkerPreparingLoggedPeer.class,
              Test_310_WorkerErrorWithPeer.class,
              Test_311_WorkerIdleWithPeer.class,
              Test_312_WorkerErrorLoggedPeer.class,
              Test_313_WorkerIdleLoggedPeer.class,
              Test_314_WorkerAllocatedForLocalBroker.class,
              Test_315_WorkerAllocatedForPeer.class,
              Test_316_WorkerLocalWorking.class,
              Test_317_WorkerAllocatedForRemoteBroker.class,
              Test_318_WorkerPreparingAllocatedForBroker.class,
              Test_319_WorkerPreparingAllocatedForPeer.class,
              Test_320_WorkerRemoteWorking.class,
              Test_321_WorkerLocalDownloading.class,
              Test_322_WorkerPreparingAllocatedForRemoteBroker.class,
              Test_323_WorkerLocalDownloadsFinished.class,
              Test_324_WorkerLocalExecute.class,
              Test_325_WorkerLocalExecuting.class,
              Test_326_WorkerLocalExecutionFinished.class,
              Test_327_WorkerLocalUploading.class,
              Test_328_WorkerLocalTaskFailed.class,
              Test_329_WorkerRemoteUploading.class,
              Test_330_WorkerRemoteTaskFailed.class,
              Test_331_WorkerRemoteExecutionFinished.class,
              Test_332_WorkerRemoteExecuting.class,
              Test_333_WorkerRemoteExecute.class,
              Test_335_WorkerRemoteDownloading.class,
              Test_334_WorkerRemoteDownloadsFinished.class
              })  
              
public class AllWorkerAcceptanceTests {}
