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
package org.ourgrid.peer.dao.trust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.TrustyPeerInfo;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.reqtrace.Req;

import com.thoughtworks.xstream.XStream;

/**
 */
@Req("REQ110")
public class TrustCommunitiesFileManipulator {
	
	private final XStream xstreamFacade;
	
	/**
	 * @param logger
	 */
	public TrustCommunitiesFileManipulator() {
		this.xstreamFacade = mountXStream();
	}
	
	private XStream mountXStream() {
		
		XStream xstream = new XStream();
        xstream.addImplicitCollection(TrustCommunitiesSack.class, "communities");
        xstream.addImplicitCollection(TrustyCommunity.class, "peers");
        xstream.alias("trusts", TrustCommunitiesSack.class);
        xstream.alias("trust", TrustyCommunity.class);
        xstream.alias("peer", TrustyPeerInfo.class);
		return xstream;
	}

	/**
	 * @param file
	 * @return
	 */
	public List<TrustyCommunity> getCommunities(List<IResponseTO> responses, File file){
		
		try {
			List<TrustyCommunity> commun = processFileToObject(file);
			String pluralORNot = (commun.size() == 1) ? "subcommunity" : "subcommunities";
			
			responses.add(new LoggerResponseTO(
					"Trust configuration file loaded with "+ commun.size() +" "+pluralORNot+".", LoggerResponseTO.INFO));
			return commun;
		} catch (InvalidFileException e) {
			responses.add(new LoggerResponseTO(
					"The trust configuration file is malformed. Ignoring all subcommunities.", LoggerResponseTO.WARN));
		} catch (IOException e) {
			responses.add(new LoggerResponseTO("Unexpected IO exception", LoggerResponseTO.ERROR, e));
		}
		
		return new LinkedList<TrustyCommunity>();
	}
	
	/**
	 * @param communities
	 * @param file
	 * @throws IOException
	 */
	public void saveOnFile(List<TrustyCommunity> communities, File file) throws IOException{
		processObjectToFile(communities, file);
	}

	/**
	 * @param communities
	 * @param file
	 * @throws InvalidFileException
	 * @throws IOException
	 */
	private void processObjectToFile(List<TrustyCommunity> communities, File file)
										throws InvalidFileException, IOException {
		
        String xml = xstreamFacade.toXML(new TrustCommunitiesSack(communities));
        
		BufferedWriter textWriter = null;
		
		try {
			textWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			textWriter.write(xml);
		} catch (Exception e) {
			throw new InvalidFileException(xml, e);
		} finally {
			if(textWriter != null) {
				textWriter.close();
			}
		}
	}
	
	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private List<TrustyCommunity> processFileToObject(File file) throws IOException{
		
		StringBuffer xmlFinal = null;
		BufferedReader textReader = null;
		try {
			textReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String inputLine;
			while ((inputLine = textReader.readLine()) != null) {
				if (null == xmlFinal) {
					xmlFinal = new StringBuffer(inputLine);
				} else {
					xmlFinal.append(inputLine);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new InvalidFileException(e);
		} catch (FileNotFoundException e) {
			throw new InvalidFileException(e);
		} catch (IOException e) {
			throw new InvalidFileException(e);
		} finally {
			if(textReader != null) {
				textReader.close();
			}
		}

		List<TrustyCommunity> communities = null;
		
		if (null != xmlFinal && !xmlFinal.toString().trim().equals("")) {
			try {
				communities = ((TrustCommunitiesSack) xstreamFacade.fromXML(xmlFinal.toString())).getCommunities();
			} catch (Throwable e) {
				throw new InvalidFileException("File <" + file + "> is in an illegal format.", e);
			}
		} else {
			throw new InvalidFileException("File <" + file + "> is in an illegal format.");
		}

		//no parser errors. The file does not contains communities
		if(communities == null){
			return new LinkedList<TrustyCommunity>();
		}
		
		if(isValid(communities)) {
			return communities;
		}

		throw new InvalidFileException();
	}

	private boolean isValid(List<TrustyCommunity> communities) {
		
		if(communities == null) {
			return false;
		}
		
		boolean allCommunitiesAreValid = true;
		
		for (TrustyCommunity trustyCommunityInfo : communities) {
			allCommunitiesAreValid &= isValid(trustyCommunityInfo);
		}
		
		return allCommunitiesAreValid;
	}

	private boolean isValid(TrustyCommunity trustyCommunityInfo) {

		if(trustyCommunityInfo == null) {
			return false;
		}
		
		boolean allEntitiesAreValid = true;
		
		for ( TrustyPeerInfo communityEntityInfo: trustyCommunityInfo.getEntities()) {
			allEntitiesAreValid &= isValid(communityEntityInfo);
		}
	
		return (allEntitiesAreValid) && (trustyCommunityInfo.getName() != null) && (trustyCommunityInfo.getPriority() >=1 );
	}

	private boolean isValid(TrustyPeerInfo communityEntityInfo) {
		
		return (communityEntityInfo == null) ? false : 
			( (communityEntityInfo.getEntityName() != null) && (communityEntityInfo.getEntityPubKey() != null));
	}

}
