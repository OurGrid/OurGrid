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
package org.ourgrid.peer.ui.async.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;

/**
 * It permites to write on the SDF File.
 */
public class SDFWriter {

	private static final String WORKER_TOKEN = "worker :";
	private static final String WORKERDEFAULTS_TOKEN = "workerdefaults :";

	/**
	 * Write the workers specs on the specified SDF file.
	 * @param specs The workers specs to be written.
	 * @param filePath The specified file path.
	 * @throws IOException If there is some problem to write on the SDF file.
	 */
	public void write(Collection<WorkerSpecification> specs, String filePath) throws IOException {
		
		//Create writer
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		
		//Search for common properties
		Map<String, String> commonAtt = CommonUtils.createSerializableMap();
		WorkerSpecification firstSpec = specs.iterator().next();
		for (String property : firstSpec.getAttributes().keySet()) {
			boolean isCommon = true;
			
			for (WorkerSpecification workerSpec : specs) {
				String value = workerSpec.getAttribute(property);
				if (value == null || !value.equals(firstSpec.getAttribute(property))) {
					isCommon = false;
					break;
				}
			}
			if (isCommon) {
				commonAtt.put(property, firstSpec.getAttribute(property));
			}
		}
		
		if (!commonAtt.isEmpty()) {
			
			writer.write(WORKERDEFAULTS_TOKEN);
			writer.newLine();
			
			for (String commonProp : commonAtt.keySet()) {
				writer.write(commonProp + " : " + commonAtt.get(commonProp));
				writer.newLine();
			}
			
			writer.newLine();
		}
		
		//write each worker
		for (WorkerSpecification workerSpec : specs) {
			writer.write(WORKER_TOKEN);
			writer.newLine();
			
			for (String prop : workerSpec.getAttributes().keySet()) {
				if (!commonAtt.containsKey(prop)) {
					writer.write("\t" + prop + " : " + workerSpec.getAttribute(prop));
					writer.newLine();
				}
			}
			
			writer.newLine();
		}
		
		writer.close();
	}	


	public void writeAds(Collection<WorkerSpecification> specs, String filePath) throws IOException {
		//Create writer
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		
		//Search for common properties
		Map<String, String> commonAtt = CommonUtils.createSerializableMap();
		WorkerSpecification firstSpec = specs.iterator().next();
		for (String property : firstSpec.getAttributes().keySet()) {
			boolean isCommon = true;
			
			for (WorkerSpecification workerSpec : specs) {
				String value = workerSpec.getAttribute(property);
				if (value == null || !value.equals(firstSpec.getAttribute(property))) {
					isCommon = false;
					break;
				}
			}
			if (isCommon) {
				commonAtt.put(property, firstSpec.getAttribute(property));
			}
		}
		
		writer.write("[");
		writer.newLine();
		
		//Common attributes
		if (!commonAtt.isEmpty()) {
			
			for (String commonProp : commonAtt.keySet()) {
				
				String value = commonAtt.get(commonProp);
				if(value.contains("{") || value.contains("}")){
					writer.write(commonProp + "=" + value+";");
				}else{
					writer.write(commonProp + "=\"" + value+"\";");
				}
			}
			
			writer.newLine();
		}
		
		//write each worker
		for (WorkerSpecification workerSpec : specs) {
			
			for (String prop : workerSpec.getAttributes().keySet()) {
				String value = workerSpec.getAttribute(prop);
				if (!commonAtt.containsKey(prop)) {
					if(value.contains("{") || value.contains("}")){
						writer.write("\t" + prop + "=" + workerSpec.getAttribute(prop)+";");
					}else{
						writer.write("\t" + prop + "=\"" + workerSpec.getAttribute(prop)+"\";");
					}
					writer.newLine();
				}
			}
			
			writer.newLine();
		}
		
		writer.write("]");
		writer.newLine();
		
		writer.close();
	}
}
