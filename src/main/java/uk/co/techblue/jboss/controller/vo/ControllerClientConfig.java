/*******************************************************************************
 * Copyright 2013 Technology Blueprint Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package uk.co.techblue.jboss.controller.vo;

import java.util.Map;

import javax.net.ssl.SSLContext;

public class ControllerClientConfig {

    private final String host;

    private final int port;

    private int connectionTimeOut = 5000;

    private String userName;

    private String password;

    private Map<String, String> saslOptions;

    private SSLContext sSLContext;

    public ControllerClientConfig(String host) {
        this.host = host;
        this.port = 9999;
    }

    public ControllerClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getSaslOptions() {
        return saslOptions;
    }

    public void setSaslOptions(Map<String, String> saslOptions) {
        this.saslOptions = saslOptions;
    }

    public SSLContext getsSLContext() {
        return sSLContext;
    }

    public void setsSLContext(SSLContext sSLContext) {
        this.sSLContext = sSLContext;
    }
}
