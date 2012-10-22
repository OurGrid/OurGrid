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
package org.ourgrid.peer.communication.dao;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.edu.ufcg.lsd.commune.network.certification.CertificateCRLPair;

/**
 *
 */
public class PeerCertificationDAO {

	private Collection<CertificateCRLPair> requestingCAsData;
	private Collection<CertificateCRLPair> receivingCAsData;
	
	
	/*public PeerCertificationDAO(Module application) {
		super(application);
		ModuleContext context = application.getContainer().getContext();
		this.requestingCAsData = CertificationUtils.loadCAsData(ContainerContextUtils.normalizeFilePath(context, 
				context.getProperty(PeerConfiguration.PROP_REQUESTING_CACERTIFICATE_PATH)));
		this.receivingCAsData = CertificationUtils.loadCAsData(ContainerContextUtils.normalizeFilePath(context, 
				context.getProperty(PeerConfiguration.PROP_RECEIVING_CACERTIFICATE_PATH)));
	}*/
	
	public PeerCertificationDAO() {
		this.requestingCAsData = new ArrayList<CertificateCRLPair>();
		this.receivingCAsData = new ArrayList<CertificateCRLPair>();
	}
	
	public Collection<CertificateCRLPair> getRequestingPeersCAsData() {
		return this.requestingCAsData;
	}
	
	public List<X509Certificate> getRequestingPeersCAsCertificates() {
		List<X509Certificate> certs = new ArrayList<X509Certificate>();
		for (CertificateCRLPair crlPair : requestingCAsData) {
			certs.add(crlPair.getCertificate());
		}
		return certs;
	}
	
	public Collection<CertificateCRLPair> getReceivedPeersCAsData() {
		return this.receivingCAsData;
	}
	
	public void setRequestingCAsData(Collection<CertificateCRLPair> requestingCAsData) {
		this.requestingCAsData = requestingCAsData;
	}
	
	public void setReceivingCAsData(Collection<CertificateCRLPair> receivingCAsData) {
		this.receivingCAsData = receivingCAsData;
	}
	
	public void addRequestingCAData(CertificateCRLPair requestingCAData) {
		this.requestingCAsData.add(requestingCAData);
	}
	
	public void addReceivingCAData(CertificateCRLPair receivingCAData) {
		this.receivingCAsData.add(receivingCAData);
	}
}
