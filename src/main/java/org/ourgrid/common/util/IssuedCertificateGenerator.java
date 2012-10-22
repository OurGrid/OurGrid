package org.ourgrid.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.util.StreamParsingException;

import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.Util;

public class IssuedCertificateGenerator {
	
	private static final String WRONG_ARGS = "Wrong number of arguments! "
								+ "Usage: IssuedCertificateGenerator certificatePath" +
								" propertiesFilePath outPutCertificatePath";

	public static void main(String[] args) throws Exception {
			
		if (args.length != 3) {
			throw new IllegalArgumentException(
					WRONG_ARGS);
		}

		issueCertificate(args[0], args[1], args[2]);
		
	}
	
	public static void issueCertificate(String certificatePath,
			String propertiesFilePath, String outPutCertificatePath) 
					throws Exception {
		
		Properties issuerProperties = loadProperties(propertiesFilePath);
		PrivateKey issuerPrivateKey = loadIssuerPrivateKey(issuerProperties);
		X509Certificate issuerCertificate = loadIssuerCertificate(issuerProperties);

		X509Certificate certificate = loadCertificate(certificatePath);
		
		X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();

		certGenerator.setSerialNumber(certificate.getSerialNumber());
		certGenerator.setPublicKey(certificate.getPublicKey());
		certGenerator.setSubjectDN(certificate.getSubjectX500Principal());
		certGenerator.setIssuerDN(issuerCertificate.getSubjectX500Principal());
		certGenerator.setNotBefore(certificate.getNotBefore());
		certGenerator.setNotAfter(certificate.getNotAfter());
		certGenerator.setSignatureAlgorithm(certificate.getSigAlgName());

		X509Certificate signedCertificate = certGenerator.generate(issuerPrivateKey);

		File file = new File(outPutCertificatePath);
		if (!file.exists()) {
			FileUtils.touch(file);
		}
		
		FileOutputStream fosP = new FileOutputStream(file); 
		fosP.write(signedCertificate.getEncoded());

		fosP.close();
		
	}

	private static X509Certificate loadCertificate(String certificatePath)
			throws FileNotFoundException, StreamParsingException {
		X509CertParser certificateParser = new X509CertParser();
		certificateParser.engineInit(new FileInputStream(certificatePath));
		X509Certificate certificate = (X509Certificate) certificateParser.engineRead();
		return certificate;
	}

	private static X509Certificate loadIssuerCertificate(Properties issuerProperties)
			throws IOException, FileNotFoundException, StreamParsingException {
		
		String issuerCertificatePath = issuerProperties.getProperty(
				FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH);
		
		X509Certificate issuerCertificate = loadCertificate(issuerCertificatePath);
		
		return issuerCertificate;
	}
	
	private static PrivateKey loadIssuerPrivateKey(Properties issuerProperties)
			throws InvalidKeySpecException {
		
		PrivateKey privateKey = Util.decodePrivateKey(issuerProperties.getProperty(
				SignatureProperties.PROP_PRIVATE_KEY));
		
		return privateKey;
	}

	private static Properties loadProperties(String propertiesFilePath)
			throws IOException, FileNotFoundException {
		Properties issuerProperties = new Properties();
		issuerProperties.load(new FileInputStream(propertiesFilePath));
		return issuerProperties;
	}
}
