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
package org.ourgrid.worker;

import java.util.Map;

import org.ourgrid.common.OurGridContextFactory;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.business.requester.ScheduleTimeParser;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.context.ContextParser;

public class WorkerComponentContextFactory extends OurGridContextFactory {

//	private final String CONF_DIR = findConfDir();
	
	public WorkerComponentContextFactory(ContextParser parser) {
		super(parser);
	}
	
	@Override
	public Map<Object, Object> getDefaultProperties() {
		
		Map<Object, Object> properties = super.getDefaultProperties();
		properties.put(ModuleProperties.PROP_CONFDIR, findConfDir());
		properties.put( WorkerConfiguration.PROP_XSESSION_IDLENESS_FILE, 
				WorkerConfiguration.DEF_PROP_XSESSION_IDLENESS_FILE );
		properties.put( WorkerConfiguration.PROP_STORAGE_DIR, 
				WorkerConfiguration.DEF_PROP_STORAGE_DIR );
		properties.put( WorkerConfiguration.PROP_PLAYPEN_ROOT, 
				WorkerConfiguration.DEF_PROP_PLAYPEN_ROOT );
		properties.put( WorkerConfiguration.PROP_IDLENESS_DETECTOR, 
				WorkerConfiguration.DEF_PROP_IDLENESS_DETECTOR );
		properties.put( WorkerConfiguration.PROP_IDLENESS_TIME, 
				WorkerConfiguration.DEF_PROP_IDLENESS_TIME );

		properties.put( WorkerConfiguration.PROP_USE_IDLENESS_SCHEDULE, 
				WorkerConfiguration.DEF_PROP_USE_IDLENESS_SCHEDULE );
		properties.put( WorkerConfiguration.PROP_IDLENESS_SCHEDULE_TIME, 
				WorkerConfiguration.DEF_PROP_IDLENESS_SCHEDULE_TIME );
		
		properties.put( WorkerConfiguration.PROP_WORKER_SPEC_REPORT, 
				WorkerConfiguration.DEF_WORKER_SPEC_REPORT );
		properties.put( WorkerConfiguration.PROP_WORKER_SPEC_REPORT_TIME, 
				WorkerConfiguration.DEF_WORKER_SPEC_REPORT_TIME );
		properties.put( WorkerConfiguration.PROP_PEER_ADDRESS,
				WorkerConfiguration.DEF_PEER_ADDRESS);

		return properties;
	}
	
	public void validate(Map<Object, Object> properties) {
		super.validate(properties);
		
		StringBuffer missingProperties = new StringBuffer();
		
//		if (properties.get( WorkerConfiguration.PROP_PEER_ADDRESS ) == null) {
//			missingProperties += WorkerConfiguration.PROP_PEER_ADDRESS;
//		}
//		
//		if (properties.get( WorkerConfiguration.PROP_PEER_VALIDATION_STRING ) == null) {
//			missingProperties += WorkerConfiguration.PROP_PEER_VALIDATION_STRING;
//		}
		
		String idlenessScheduleTime =
			(String) properties.get(WorkerConfiguration.PROP_IDLENESS_SCHEDULE_TIME);
		
		ScheduleTimeParser scheduleTimeParser = new ScheduleTimeParser(idlenessScheduleTime);
		scheduleTimeParser.parseScheduleTimes();
		
		if (missingProperties.length() > 0) {
			throw new CommuneRuntimeException("Context could not be loaded. " +
					"The following mandatory properties are missing: " + missingProperties + 
					"\nSet up them in \"Worker Configuration\" tab then click \"save\" button.");
		}
		
	}
	
	@Req("REQ003")
	protected String findConfDir() {

		String prop = System.getenv( "OGROOT" );
		
		if ( prop == null || prop.equals( "" ) ) {
			prop = System.getProperty( "user.dir" );
		}
		return prop;
	}
	
}
