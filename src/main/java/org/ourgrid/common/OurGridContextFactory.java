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
package org.ourgrid.common;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.util.SelfSignedCertificateGenerator;

import br.edu.ufcg.lsd.commune.context.ContainerContextUtils;
import br.edu.ufcg.lsd.commune.context.ContextParser;
import br.edu.ufcg.lsd.commune.context.DefaultContextFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.Util;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;

public class OurGridContextFactory extends DefaultContextFactory {

	public static final String MYCERTIFICATE_DEF_PATH = "certification" + File.separator + 
		"mycertificate" + File.separator + "mycertificate.cer";
	
	public OurGridContextFactory(ContextParser parser) {
		super(parser);
	}
	
	@Override
	public ModuleContext createContext() {
		
		ModuleContext createdContext = super.createContext();
		
		String certFilePath = ContainerContextUtils.normalizeFilePath(createdContext, createdContext.getProperty(
				FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH));
		
		if (!(new File(certFilePath).exists()) ) {
			
			try {
				
				PrivateKey privateKey = Util.decodePrivateKey(createdContext.getProperty(
						SignatureProperties.PROP_PRIVATE_KEY));
				PublicKey publicKey = Util.decodePublicKey(createdContext.getProperty(
						SignatureProperties.PROP_PUBLIC_KEY));
				KeyPair keyPair = new KeyPair(publicKey, privateKey);
				
				String userName = createdContext.getProperty(XMPPProperties.PROP_USERNAME);
				String serverName = createdContext.getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
				
				SelfSignedCertificateGenerator.generateX509Certificate(keyPair, 
						getCertificateDN(userName, serverName), certFilePath);
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}
		
		return createdContext;
	}
	
	public static String getCertificateDN(String user, String server) {
		return "CN=" + user + ",OU=" + server;
	}
	
	@Override
	public Map<Object, Object> getDefaultProperties() {
		Map<Object, Object> properties = super.getDefaultProperties();
		
		properties.put(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				findConfDir() + File.separator + MYCERTIFICATE_DEF_PATH);
		
		
		return properties;
	}
	
	protected String findConfDir() {
		return System.getenv(Configuration.OGROOT);

	}
	
}
