/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */
package org.eclipse.paho.client.mqttv3.internal;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

/**
 * Provides a mechanism for creating a NetworkModule
 */
public class MqttMicroNetworkFactory extends AbstractMqttNetworkFactory {
	
	private static final int URI_TYPE_TCP = 0;
	private static final int URI_TYPE_SSL = 1;
	private static final int URI_TYPE_LOCAL = 2;

	/**
	 * Factory method to create the correct network module, based on the
	 * supplied address URI.
	 * 
	 * @param address the URI for the server.
	 * @param Connect options 
	 * @return a network module appropriate to the specified address.
	 */
	public NetworkModule createNetworkModule(String address, MqttConnectOptions options, String clientId) throws MqttException {
		final String methodName = "createNetworkModule";
		// @TRACE 115=URI={0}
		log.fine(CLASS_NAME, methodName, "115", new Object[] { address });

		NetworkModule netModule;
		String shortAddress;
		String host;
		int port;
		
		int serverURIType = validateURI(address);
		// @ADAM A01 - May need to introduce SSL in here.
		switch (serverURIType) {
		case URI_TYPE_TCP:
			shortAddress = address.substring(6);
			host = getHostName(shortAddress);
			port = getPort(shortAddress, 1883);
			netModule = new TCPMicroNetworkModule(host, port, options.getGprsConnectOptions());
			break;
		case URI_TYPE_SSL:
			shortAddress = address.substring(6);
			host = getHostName(shortAddress);
			port = getPort(shortAddress, 8883);
			// SSL configuration for Java ME is much simpler, and we'll use
			// the platform settings.
			netModule = new SSLMicroNetworkModule(host, port, options.getGprsConnectOptions());
			break;
		default:
			// This shouldn't happen, as long as validateURI() has been called.
			netModule = null;
		}
		return netModule;
	}

	/**
	 * Validate a URI 
	 * @param srvURI
	 * @return the URI type
	 */
	protected static int validateURI(String srvURI) {
		if (srvURI.startsWith("tcp://")) {
			return URI_TYPE_TCP;
		}
		else if (srvURI.startsWith("ssl://")) {
			return URI_TYPE_SSL;
		}
		else if (srvURI.startsWith("local://")) {
			return URI_TYPE_LOCAL;
		}
		else {
			throw new IllegalArgumentException(srvURI);
		}
	}

}
