package org.eclipse.paho.client.mqttv3.util;

public class GprsConnectOptions {

  private String bearerType;
  private String accessPoint;
  private String username;
  private String password;
  private String timeout;

  public void setBearerType(String bearerType) {
    this.bearerType = bearerType;
  }

  public void setAccessPoint(String accessPoint) {
    this.accessPoint = accessPoint;
  }

  public void setTimeout(int timeout) {
    this.timeout = String.valueOf(timeout);
  }

  public void setUser(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public static String toUriParameters(GprsConnectOptions options) {
    return (options != null ? options.toUriParameters() : "");
  }

  public String toUriParameters() {
    StringBuffer sb = new StringBuffer(127);
    appendParameter(sb, "bearer_type", this.bearerType);
    appendParameter(sb, "access_point", this.accessPoint);
    appendParameter(sb, "username", this.username);
    appendParameter(sb, "password", this.password);
    appendParameter(sb, "timeout", this.timeout);
    return sb.toString();
  }

  private static void appendParameter(StringBuffer sb, String paramName, String value) {
    if (value != null) {
      sb.append(';').append(paramName).append('=').append(value);
    }
  }

  public String toString() {
    return toUriParameters();
  }

}
