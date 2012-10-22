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
package org.ourgrid.peer.business.controller.messages;


public class DiscoveryServiceClientMessages {

	public static String getSameCertificateDNMessage(String serviceID) {
		return "Disposing worker provider [" + 
				serviceID + "] because it has the same certificate " +
						"DN of this peer.";
	}

	public static String getInvalidCertPathMessage(String workerProviderID) {
		return "Disposing worker provider [" + workerProviderID + "] because " +
				"it has an invalid Certificate Path.";
	}

	public static String getNonIssuedCertPathMessage(
			String workerProviderID) {
		return "Disposing worker provider [" + workerProviderID + "] because it " +
				"has an non issued Certificate Path.";
	}
	
	
}
