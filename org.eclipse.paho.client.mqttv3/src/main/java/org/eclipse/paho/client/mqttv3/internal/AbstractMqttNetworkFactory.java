/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
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
 *    Neeme Praks - moved JavaSE and JavaME common networking code to this class.
 */
package org.eclipse.paho.client.mqttv3.internal;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

public abstract class AbstractMqttNetworkFactory implements IMqttNetworkFactory {

  protected final String CLASS_NAME = getClass().getName();
  protected final Logger log = LoggerFactory.getLogger(LoggerFactory.MQTT_CLIENT_MSG_CAT, CLASS_NAME);

  // may need an array of these network modules
  public NetworkModule[] createNetworkModules(String address, MqttConnectOptions options, String clientId) throws MqttException,
    MqttSecurityException {
    final String methodName = "createNetworkModules";
    // @TRACE 116=URI={0}
    log.fine(CLASS_NAME, methodName, "116", new Object[]{address});

    NetworkModule[] networkModules = null;
    String[] serverURIs = options.getServerURIs();
    String[] array = null;
    if (serverURIs == null) {
      array = new String[]{address};
    } else if (serverURIs.length == 0) {
      array = new String[]{address};
    } else {
      array = serverURIs;
    }

    networkModules = new NetworkModule[array.length];
    for (int i = 0; i < array.length; i++) {
      networkModules[i] = createNetworkModule(array[i], options, clientId);
    }

    log.fine(CLASS_NAME, methodName, "108");
    return networkModules;
  }

  /**
   * Factory method to create the correct network module, based on the
   * supplied address URI.
   *
   * @param address the URI for the server.
   * @param options options
   * @return a network module appropriate to the specified address.
   */
  protected abstract NetworkModule createNetworkModule(String address, MqttConnectOptions options, String clientId) throws MqttException;

  protected static int getPort(String uri, int defaultPort) {
    int port;
    int portIndex = uri.lastIndexOf(':');
    if (portIndex == -1) {
      port = defaultPort;
    }
    else {
      int slashIndex = uri.indexOf('/');
      if (slashIndex == -1) {
        slashIndex = uri.length();
      }
      port = Integer.parseInt(uri.substring(portIndex + 1, slashIndex));
    }
    return port;
  }

  protected static String getHostName(String uri) {
    int portIndex = uri.indexOf(':');
    if (portIndex == -1) {
      portIndex = uri.indexOf('/');
    }
    if (portIndex == -1) {
      portIndex = uri.length();
    }
    return uri.substring(0, portIndex);
  }

}
