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
package org.ourgrid.deployer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.deployer.xmpp.KeyPairString;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.Util;

public class Deployer {

    /**
     * Loads properties from a persistent File
     * @param propFile The File where the properties are stored
     * @return a Properties object containing the persistent properties
     */
    public Properties loadProperties(File propFile) {
        return CommonUtils.loadProperties(propFile);
    }
    
    /**
     * Gets the local domain of the local machine
     * @return the local domain of the local machine
     */
    @Req({"REQ100", "REQ101"})
    public static String getLocalDomain() {
        
        try {
            String fullAddress = InetAddress.getLocalHost().getCanonicalHostName();
            String hostname = InetAddress.getLocalHost().getHostName();
            return fullAddress.replaceFirst(hostname + ".", "");
        } catch ( UnknownHostException e ) {
            return "localhost";
        }
    }
    
    /**
     * Stores a Properties object on a persistent File
     * @param properties The Properties object
     * @param propFile The File where the Properties should be stored
     * @param propertiesName The name of this properties (will be on the file header)
     */
    public void saveProperties(Properties properties, File propFile, String propertiesName) throws IOException {
        CommonUtils.saveProperties(properties, propFile, propertiesName);
    }

    KeyPairString generateKeyPair() {
        
        KeyPair pair = Util.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        
        return new KeyPairString(Util.encodeArrayToBase64String(priv.getEncoded()), 
                    Util.encodeArrayToBase64String(pub.getEncoded()));
    }
    
    KeyPairString loadKeyPair(String path) {
        Properties keyStore = loadProperties(new File(path));

        return new KeyPairString(keyStore.getProperty(SignatureProperties.PROP_PRIVATE_KEY), 
                    keyStore.getProperty(SignatureProperties.PROP_PUBLIC_KEY));
    }

    protected Logger getLogger() {
        return Logger.getLogger( getClass() );
    }

}
