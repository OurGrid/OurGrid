package org.ourgrid.broker.controlws;

import javax.xml.ws.Endpoint;

public class WebServicePublisher {
	
	public static void main(String[] args) {
		Endpoint.publish("http://localhost:8080/BrokerControl",
		new BrokerControlWS());
	}

}
