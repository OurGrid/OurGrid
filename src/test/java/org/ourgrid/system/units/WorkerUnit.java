package org.ourgrid.system.units;

import static java.io.File.separator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerComponentContextFactory;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.ui.sync.WorkerSyncComponentClient;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class WorkerUnit extends AbstractUnit {
	
	private static final String TEMP_DIR = System.getProperty( "java.io.tmpdir" );

	private String playpenRootPath;

	private String storagePath;

	private boolean idlenessActivated;

	private int idlenessTime;

	private Map<String,String> attributes;

	private WorkerSyncComponentClient uiManager;

	private ServiceID workerServiceID;
	
	public static final String ATT_REM_EXEC = "ssh -o StrictHostKeyChecking=no -x $machine $command";

	public static final String ATT_COPY_FROM = "scp -o StrictHostKeyChecking=no $machine:$remotefile $localfile";

	public static final String ATT_COPY_TO = "scp -o StrictHostKeyChecking=no $localfile $machine:$remotefile";
	
	private static final String WORKER_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "worker.properties";
	
	public WorkerUnit(String propertiesFile) throws Exception {
		super(WorkerConstants.MODULE_NAME, propertiesFile);
		initData(TEMP_DIR, TEMP_DIR + File.separator + UUID.randomUUID().toString(), false, 0);
		setContext(createContext());
		getUIManager();
		this.uiManager.start();
	}
	
	protected WorkerUnit() throws Exception {

		this( TEMP_DIR, TEMP_DIR + File.separator + UUID.randomUUID().toString(), false, 0 );
	}


	protected WorkerUnit( String playpenRootPath, String storagePath, boolean idlenessActivated, int idlenessTime )
		throws Exception {

		super( WorkerConstants.MODULE_NAME );
		initData(playpenRootPath, storagePath, idlenessActivated, idlenessTime);
		getUIManager();
		this.uiManager.start();
	}
	
	private void initData(String playpenRootPath, String storagePath, boolean idlenessActivated, int idlenessTime) 
		throws Exception {
		this.playpenRootPath = playpenRootPath;
		this.storagePath = storagePath;
		this.idlenessActivated = idlenessActivated;
		this.idlenessTime = idlenessTime;
		this.attributes = new HashMap<String,String>();
		setContext(createContext());
		contextCreated();
	}
	
	@Override
	public WorkerSyncComponentClient getUIManager() throws Exception {

		if ( this.uiManager == null ) {
			ModuleContext context = getContext();
			
			this.workerServiceID = new ServiceID(getJabberUserName(), getJabberServerHostname(), 
					WorkerConstants.MODULE_NAME , WorkerConstants.WORKER);
			
			Map<String, String> properties = context.getProperties();
			properties.put(WorkerConstants.PROP_PLAYPEN_ROOT, playpenRootPath);
			properties.put(WorkerConstants.PROP_STORAGE_DIR, storagePath);
			properties.put(WorkerConstants.PROP_IDLENESS_DETECTOR, String.valueOf(idlenessActivated));
			properties.put(WorkerConstants.PROP_IDLENESS_TIME, String.valueOf(idlenessTime));
			
			context = new ModuleContext(properties);
			
			this.uiManager = new WorkerSyncComponentClient(context);
		}
		return this.uiManager;
	}
	
	public void start() throws Exception {
		initKeys();
		createComponent();
		getUIManager();
		this.uiManager.start();
	}


	public DeploymentID getMasterPeer() throws Exception {
		String peerUserAtServer = uiManager.getWorkerCompleteStatus().getPeerInfo().getPeerUserAtServer();
		String[] splitAddress = StringUtil.splitAddress(peerUserAtServer);
		
		ServiceID serviceID = new ServiceID(splitAddress[0], splitAddress[1], PeerConstants.MODULE_NAME,
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		
		return new DeploymentID(serviceID);
		
	}


	public void cleanUp() throws Exception {

		JavaFileUtil.deleteDir( storagePath );
	}


	public WorkerCompleteStatus getStatus() throws Exception {

		return uiManager.getWorkerCompleteStatus();
	}


	public String getCurrentPlaypenDir() throws Exception {

		return uiManager.getWorkerCompleteStatus().getCurrentPlaypenDirPath();
	}


	public boolean isTheSameEntity( ServiceID serviceID ) {

		return this.workerServiceID.equals( serviceID ); 
	}


	public void setPlaypenRootPath( String playpenRootPath ) {

		this.playpenRootPath = playpenRootPath;
	}


	public void setStorageRootPath( String playpenRootPath ) {

		this.storagePath = playpenRootPath;
	}


	public void addProperty( String attName, String attValue ) {

		this.attributes.put( attName, attValue );
	}


	public Map<String,String> getRequirements() {

		return this.attributes;
	}


	@Override
	protected void deploy() {

/*		ArrayList<WorkerSpec> specs = new ArrayList<WorkerSpec>();
		specs.add( getSpec() );
		org.ourgrid.deployer.Deployer.runWorkerDeployer( "start", UnitManager.getInstance().getDeployDirPrefix(),
			UnitManager.getInstance().getRootDir(), true, specs );*/
	}


	public WorkerSpecification getSpec() {

		WorkerSpecification spec = new WorkerSpecification();
		spec.putAttribute( OurGridSpecificationConstants.ATT_USERNAME, getJabberUserName() );
		spec.putAttribute( OurGridSpecificationConstants.ATT_SERVERNAME, getJabberServerHostname() );
		//spec.putAttribute( WorkerSpec.ATT_MACHINE, getHostMachine() );
		spec.putAttribute( OurGridSpecificationConstants.ATT_PASSWORD, getJabberUserName() );
		spec.putAttribute( OurGridSpecificationConstants.ATT_REM_EXEC, ATT_REM_EXEC );
		spec.putAttribute( OurGridSpecificationConstants.ATT_COPY_FROM, ATT_COPY_FROM );
		spec.putAttribute( OurGridSpecificationConstants.ATT_COPY_TO, ATT_COPY_TO );
		for ( Entry<String,String> entry : getRequirements().entrySet() ) {
			spec.putAttribute( entry.getKey(), entry.getValue() );
		}
		return spec;
	}


	public void pause() throws Exception {

		System.out.println( "PAUSING: " + this.toString() );

		getUIManager();
		this.uiManager.pauseWorker();
	}


	public void resume() throws Exception {

		System.out.println( "RESUMING: " + this.toString() );

		getUIManager();
		this.uiManager.resumeWorker();
	}


	@Override
	protected void createComponent() {
		ModuleContext context = getContext();
		
		try {
			new WorkerComponent(context);
		} catch (CommuneNetworkException e) {
			e.printStackTrace();
		} catch (ProcessorStartException e) {
			e.printStackTrace();
		}
	}


	protected ModuleContext createContext() {
		String propertiesFile = WORKER_PROPERTIES_FILENAME;
		if (this.propertiesFile != null) {
			propertiesFile = this.propertiesFile;
		}	
		
		WorkerComponentContextFactory contextFactory = new WorkerComponentContextFactory(
				new PropertiesFileParser(propertiesFile));
			
		return contextFactory.createContext();
	}
	
}
