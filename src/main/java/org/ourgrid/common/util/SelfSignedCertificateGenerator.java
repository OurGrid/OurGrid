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
package org.ourgrid.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import br.edu.ufcg.lsd.commune.network.signature.SignatureConstants;
import br.edu.ufcg.lsd.commune.network.signature.Util;

public class SelfSignedCertificateGenerator {

	private static long VALIDITY_INTERVAL = 1000L * 60 * 60 * 24 * 365; //one year

	/**
	 * Generates a self-signed certificate based on a DN data
	 * @param args Certificate DN data, generated certificate path.
	 * @throws IOException 
	 * @throws SignatureException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IllegalStateException 
	 * @throws InvalidKeyException 
	 * @throws CertificateEncodingException 
	 */
	public static void main(String[] args) throws CertificateEncodingException, InvalidKeyException, 
		IllegalStateException, NoSuchAlgorithmException, SignatureException, IOException {
	
		if (args.length != 2) {
			throw new IllegalArgumentException("Wrong number of arguments! " +
					"Usage: SelfSignedCertificateGenerator DNData CertificatePath");
		}
		
			generateX509Certificate(Util.generateKeyPair(), 
					args[0], args[1]);
	}
	
	public static void generateX509Certificate(KeyPair keyPair, String dnData, 
			String certFilePath) 
	throws CertificateEncodingException, InvalidKeyException, IllegalStateException, 
	NoSuchAlgorithmException, SignatureException, IOException {

		X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();

		certGenerator.setSerialNumber(BigInteger.valueOf(Math.abs(new Random().nextLong())));
		certGenerator.setPublicKey(keyPair.getPublic());
		certGenerator.setSubjectDN(new X500Principal(dnData));
		certGenerator.setIssuerDN(new X500Principal(dnData));
		certGenerator.setNotBefore(new Date(System.currentTimeMillis()
				- VALIDITY_INTERVAL));
		certGenerator.setNotAfter(new Date(System.currentTimeMillis()
				+ VALIDITY_INTERVAL));
		certGenerator.setSignatureAlgorithm(SignatureConstants.SIGN_ALGORITHM );

		X509Certificate certificate = certGenerator.generate(keyPair.getPrivate());

		File file = new File(certFilePath);
		if (!file.exists()) {
			FileUtils.touch(file);
		}
		
		FileOutputStream fosP = new FileOutputStream(file); 
		fosP.write(certificate.getEncoded());

		fosP.close();
	}

}
