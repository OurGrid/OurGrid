package org.ourgrid.broker.controlws.gatewayws;

import java.io.File;

public interface Broker3GConstants {

	public static final String JOBSDIR = System.getProperty("java.io.tmpdir") + File.separator + "jobs3G";
	
	public static final String BROKER_3G_TRANSFERPORT_PROP = "broker.3g.transferport";

	public static final String BROKER_3G_TMPDIR_PROP = "broker.3g.tmpdir";

	public static final String BROKER_3G_TRANSFERPORT_DEF = "8585";
	
	public static final String LOG_CATEGORY = "WS3GAPPENDER";

}
