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

/**
 * The server Model Controller client configuration properties.
 * 
 * @author <a href="mailto:ajay.deshwal@techblue.co.uk">Ajay Deshwal</a>
 */
public class ControllerClientConfig {

    /** The host. */
    private final String host;

    /** The port. */
    private final int port;

    /** The connection time out. */
    private int connectionTimeOut = 5000;

    /** The user name. */
    private String userName;

    /** The password. */
    private String password;

    /** The sasl options. */
    private Map<String, String> saslOptions;

    /** The s sl context. */
    private SSLContext sslContext;

    /**
     * Instantiates a new controller client config.
     * 
     * @param host the address of the controller host
     */
    public ControllerClientConfig(String host) {
        this.host = host;
        this.port = 9990;
    }

    /**
     * Instantiates a new controller client config.
     * 
     * @param host the address of the controller host
     * @param port the port of the controller host.
     */
    public ControllerClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Gets the address of the controller host.
     * 
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port of the controller host.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the connection timeout when trying to connect to the server.
     * 
     * @return the connection time out
     */
    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    /**
     * Sets the connection timeout when trying to connect to the server.
     * 
     * @param connectionTimeOut the new connection time out
     */
    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * Gets the server management user name.
     * 
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets server management user name.
     * 
     * @param userName the new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets server management password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets server management password.
     * 
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the sasl options.
     * 
     * @return the sasl options
     */
    public Map<String, String> getSaslOptions() {
        return saslOptions;
    }

    /**
     * Sets the sasl options.
     * 
     * @param saslOptions the sasl options
     */
    public void setSaslOptions(Map<String, String> saslOptions) {
        this.saslOptions = saslOptions;
    }

    /**
     * Gets the ssl context.
     * 
     * @return the ssl context
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * Sets the ssl context.
     * 
     * @param sSLContext the new ssl context
     */
    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }
}
