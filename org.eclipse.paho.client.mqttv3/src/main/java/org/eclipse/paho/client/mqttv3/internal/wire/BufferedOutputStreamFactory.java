package org.eclipse.paho.client.mqttv3.internal.wire;

import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

import java.io.OutputStream;

/**
 * Wraps
 */
public class BufferedOutputStreamFactory {

  private static final String CLASS_NAME = BufferedOutputStreamFactory.class.getName();
  private static final Logger log = LoggerFactory.getLogger(LoggerFactory.MQTT_CLIENT_MSG_CAT, CLASS_NAME);

  private static final Factory IMPL = findImplementation(new String[] {
    CLASS_NAME + "$JavaIOFactory",
    CLASS_NAME + "$MidpIOFactory"
  });

  private static Factory findImplementation(String[] implementations) {
    for (int i = 0; i < implementations.length; i++) {
      String implClassName = implementations[i];
      try {
        return (Factory) Class.forName(implClassName).newInstance();
      }
      catch (ClassNotFoundException e) {
        log.fine(CLASS_NAME, "static", "Could not load class: " + e);
      }
      catch (InstantiationException e) {
        log.fine(CLASS_NAME, "static", "Could not instantiate class: " + e);
      }
      catch (IllegalAccessException e) {
        log.fine(CLASS_NAME, "static", "Could not access class: " + e);
      }
    }
    return null;
  }

  public static OutputStream wrap(OutputStream os) {
    if (IMPL != null) {
      return IMPL.wrap(os);
    }
    else {
      return os;
    }
  }

  private interface Factory {
    OutputStream wrap(OutputStream os);
  }

//  private static class JavaIOFactory implements Factory {
//
//    public OutputStream wrap(OutputStream os) {
//      return new java.io.BufferedOutputStream(os);
//    }
//  }

  private static class MidpIOFactory implements Factory {

    public OutputStream wrap(OutputStream os) {
      return new com.sun.midp.io.BufferedOutputStream(os);
    }
  }

}
