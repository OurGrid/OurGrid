package org.ourgrid.common.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.ourgrid.common.OurGridContextFactory;

import br.edu.ufcg.lsd.commune.network.signature.Util;

public class SelfSignedSetup {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			throw new IllegalArgumentException("Wrong number of arguments! " +
					"Usage: SelfSignedSetup user server certpath");
		}
		
		generateX509Certificate(args[0], args[1], args[2]);
	}

	private static void generateX509Certificate(String user, String server,
			String certificatePath) throws Exception {
		KeyPair generatedKeyPair = Util.generateKeyPair();
		String dnData = OurGridContextFactory.getCertificateDN(user, server);
		SelfSignedCertificateGenerator.generateX509Certificate(
				generatedKeyPair, dnData, certificatePath);
		printKeyPair(generatedKeyPair);
	}
	
	private static void printKeyPair(KeyPair keyPair) {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		String encodedPublicKey = Util.encodeArrayToBase64String(
				publicKey.getEncoded());
		String encodedPrivateKey = Util.encodeArrayToBase64String(
				privateKey.getEncoded());
		String encodedKeyPair = encodedPublicKey + " " + encodedPrivateKey;
		
		System.out.println(encodedKeyPair);
	}
}
