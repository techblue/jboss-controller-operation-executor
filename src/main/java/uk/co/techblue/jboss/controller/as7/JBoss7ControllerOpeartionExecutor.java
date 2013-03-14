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
package uk.co.techblue.jboss.controller.as7;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.security.auth.callback.CallbackHandler;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.controller.client.impl.ClientConfigurationImpl;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.techblue.jboss.auth.AuthenticationCallbackHandler;
import uk.co.techblue.jboss.controller.ControllerOperationExecutor;
import uk.co.techblue.jboss.controller.exception.ControllerOperationException;
import uk.co.techblue.jboss.controller.vo.ControllerClientConfig;
import uk.co.techblue.jboss.controller.vo.JndiDataSource;
import uk.co.techblue.jboss.util.StringUtils;
import static uk.co.techblue.jboss.controller.ControllerConstants.*;

/**
 * The service to execute operations on JBoss AS 7 management model controller.
 * 
 * @author <a href="mailto:ajay.deshwal@techblue.co.uk">Ajay Deshwal</a>
 */
public class JBoss7ControllerOpeartionExecutor implements ControllerOperationExecutor {

    /** The logger instance. */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The main method. ONLY FOR TESTING!
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
          final JndiDataSource dataSource = new JndiDataSource("java:/mysql-propcosystem-testjboss7", "jdbc:mysql://localhost:3306/propcosystem-testjboss7",
                    "com.mysql", "root", "admin");
//            dataSource.setMaxPoolSize(10);
//            dataSource.setPoolPrefill(true);
          final ControllerClientConfig clientConfig = new ControllerClientConfig("127.0.0.1");
//            clientConfig.setUserName("ajay");
//            clientConfig.setPassword("ajay");
          new JBoss7ControllerOpeartionExecutor().removeDatasource(clientConfig, "mysql-propcosystem-testjboss7","");
//            new JBoss7ControllerOpeartionExecutor().createDatasource(clientConfig, dataSource, true);
//            new JBoss7ControllerOpeartionExecutor().isDatasourceExists(clientConfig, dataSource.getJndiName());
            // new JBoss7ControllerOpeartionExecutor().createDatasource(clientConfig, dataSource, true, "full-ha");
            // new JBoss7ControllerOpeartionExecutor().isDatasourceExists(clientConfig, dataSource.getJndiName(), "full-ha");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#createDatasource(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, uk.co.techblue.jboss.controller.vo.JndiDataSource, boolean, java.lang.String[])
     */
    public void createDatasource(final ControllerClientConfig controllerClientConfig, final JndiDataSource dataSource,
            final boolean enable, final String... serverProfileNames) throws ControllerOperationException {
        if (serverProfileNames != null && serverProfileNames.length > 0) {
            for (final String serverProfile : serverProfileNames) {
                createDatasource(controllerClientConfig, dataSource, enable, serverProfile);
            }
        } else {
            createDatasource(controllerClientConfig, dataSource, enable, "");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#createDatasource(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, uk.co.techblue.jboss.controller.vo.JndiDataSource, boolean, java.lang.String)
     */
    private void createDatasource(final ControllerClientConfig controllerClientConfig, final JndiDataSource dataSource,
            final boolean enable, final String serverProfileName) throws ControllerOperationException {
        final String jndiName = dataSource.getJndiName();
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(ClientConstants.ADD);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_DATASOURCE, jndiName);

        addDatasourceProperties(request, dataSource);

        final ModelControllerClient client = createControllerClient(controllerClientConfig);
        ModelNode response = null;
        try {
            logger.info("Adding datasource '{}' ...", jndiName);
            response = client.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException("An error occurred while executing operation on JBoss controller", ioe);
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                logger.error(
                        "An error occurred when closing JBoss Controller connection with host {} at port {} while adding datasource '"
                                + jndiName + "'", ioe);
            }
        }
        if (!isOperationSuccess(response)) {
            if (!response.isDefined()) {
                throw new ControllerOperationException(
                        "A subsystem undefined response status recieved while adding datasource '" + jndiName
                                + "'. Most probably the " + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
            }
            logger.error("Operation rolled back:" + response.get(RESPONSE_PROPERTY_ROLLEDBACK));
            throw new ControllerOperationException("An error occurred while adding datasource '" + jndiName + ".\n"
                    + response.get(ClientConstants.FAILURE_DESCRIPTION).asString());
        }
        logger.info("Datasource '{}' added successfully!", jndiName);
        if (enable) {
            enableDataSource(controllerClientConfig, jndiName, serverProfileName);
        }
    }

    /* (non-Javadoc)
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#removeDatasource(uk.co.techblue.jboss.controller.vo.ControllerClientConfig, uk.co.techblue.jboss.controller.vo.JndiDataSource, java.lang.String[])
     */
    public void removeDatasource(final ControllerClientConfig controllerClientConfig, final String datasourceName,
            final String... serverProfileNames) throws ControllerOperationException {
        if (serverProfileNames != null && serverProfileNames.length > 0) {
            for (final String serverProfile : serverProfileNames) {
            	removeDatasource(controllerClientConfig, datasourceName, serverProfile);
            }
        } else {
        	removeDatasource(controllerClientConfig, datasourceName, "");
        }
    }
    
    /**
     * Removes datasource.
     *
     * @param controllerClientConfig the controller client config
     * @param dataSource the data source
     * @param serverProfileName the server profile name
     * @throws ControllerOperationException the controller operation exception
     */
    private void removeDatasource(final ControllerClientConfig controllerClientConfig, final String datasourceName,
    		final String serverProfileName) throws ControllerOperationException {
		final ModelNode request = new ModelNode();
		request.get(ClientConstants.OP).set(
				ClientConstants.DEPLOYMENT_REMOVE_OPERATION);
		if (StringUtils.isNotBlank(serverProfileName)) {
			request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE,
					serverProfileName);
		}
		request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM,
				DATASOURCE_SUBSYSTEM);
		request.get(ClientConstants.OP_ADDR).add(ADDRESS_DATASOURCE, datasourceName);
		final ModelControllerClient client = createControllerClient(controllerClientConfig);
		ModelNode response = null;
		try {
			logger.info("Removing datasource '{}' ...", datasourceName);
			response = client.execute(new OperationBuilder(request).build());
		} catch (IOException ioe) {
			throw new ControllerOperationException(
					"An error occurred while removing datatsource '"+datasourceName+"' from JBoss model controller",
					ioe);
		} finally {
			try {
				client.close();
			} catch (IOException ioe) {
				logger.error(
						"An error occurred when closing JBoss Controller connection with host {} at port {} while removing datasource '"
								+ datasourceName + "'", ioe);
			}
		}
		if (!isOperationSuccess(response)) {
			if (!response.isDefined()) {
				throw new ControllerOperationException(
						"A subsystem undefined response status recieved while removing datasource '"
								+ datasourceName + "'. Most probably the "
								+ DATASOURCE_SUBSYSTEM
								+ " subsystem is not defined.");
			}
			logger.error("Operation rolled back:"
					+ response.get(RESPONSE_PROPERTY_ROLLEDBACK));
			throw new ControllerOperationException(
					"An error occurred while removing datasource '"
							+ datasourceName
							+ ".\n"
							+ response.get(ClientConstants.FAILURE_DESCRIPTION)
									.asString());
		}
		logger.info("Datasource '{}' removed successfully!", datasourceName);
	}	
    
    /**
     * Creates the datasource request.
     * 
     * @param request the request
     * @param dataSource the data source
     */
    private void addDatasourceProperties(final ModelNode request, final JndiDataSource dataSource) {
        // DS ATTRIBUTES
        request.get(DS_PROPERTY_JNDINAME).set(dataSource.getJndiName());
        request.get(DS_PROPERTY_USEJAVACONTEXT).set(dataSource.isUseJavaContext());
        request.get(DS_PROPERTY_SHARE_PREPARED_STATEMENTS).set(dataSource.isSharePreparedStatements());
        request.get(DS_PROPERTY_PREPARED_STATEMENTS_CACHE_SIZE).set(dataSource.getStatementCacheSize());
        request.get(DS_PROPERTY_POOLNAME).set(dataSource.getPoolName());

        // CONNECTION PROPERTIES
        request.get(DS_PROPERTY_CONNECTIONURL).set(dataSource.getConnectionURL());
        setPropertyIfNotNull(request, DS_PROPERTY_NEWCONNECTIONSQL, dataSource.getNewConnectionSQL());
        request.get(DS_PROPERTY_TRANSACTIONISOLATION).set(dataSource.getTransactionIsolation().name());
        request.get(DS_PROPERTY_USE_CCM).set(dataSource.isUseCacheConnectionManager());
        request.get(DS_PROPERTY_JTA_INTEGRATION).set(dataSource.isUseJTA());

        // SECURITY
        request.get(DS_PROPERTY_DRIVERNAME).set(dataSource.getDriverName());
        request.get(DS_PROPERTY_USERNAME).set(dataSource.getUserName());
        request.get(DS_PROPERTY_PASSWORD).set(dataSource.getPassword());
        setPropertyIfNotNull(request, DS_PROPERTY_SECURITYDOMAIN, dataSource.getSecurityDomain());

        // POOL PROPERTIES
        request.get(DS_PROPERTY_MINPOOLSIZE).set(dataSource.getMinPoolSize());
        request.get(DS_PROPERTY_MAXPOOLSIZE).set(dataSource.getMaxPoolSize());
        request.get(DS_PROPERTY_POOLPREFILL).set(dataSource.isPoolPrefill());
        request.get(DS_PROPERTY_POOL_STRICT_MINIMUM).set(dataSource.isPoolStrictMinimum());

        // VALIDATION ATTRIBUTES
        setPropertyIfNotNull(request, DS_PROPERTY_VALID_CONNECTION_SQL, dataSource.getCheckValidSQL());
        setPropertyIfNotNull(request, DS_PROPERTY_VALID_CONNCHECKER_CLASSNAME, dataSource.getValidConnectionCheckerClassName());
        setPropertyIfNotNull(request, DS_PROPERTY_EXCEPTION_SORTER_CLASSNAME, dataSource.getExceptionSorterClassName());
        setPropertyIfNotNull(request, DS_PROPERTY_STALE_CONNCHECKER_CLASSNAME, dataSource.getStaleConnectionCheckerClassName());
        request.get(DS_PROPERTY_BG_VALIDATION).set(dataSource.isBackgroundValidation());
        if (dataSource.getBackgroundValidationMillisec() > 0) {
            request.get(DS_PROPERTY_BG_VALIDATION_MILLIS).set(dataSource.getBackgroundValidationMillisec());
        }
        request.get(DS_PROPERTY_VALIDATE_ON_MATCH).set(dataSource.isValidateOnMatch());
    }

    private void setPropertyIfNotNull(ModelNode request, String propertyName, String propertyValue) {
        if (propertyValue != null) {
            request.get(propertyName).set(propertyValue);
        }
    }

    /**
     * Creates the controller client.
     * 
     * @param controllerClientConfig the controller client configuration
     * @return the model controller client
     * @throws ControllerOperationException the controller operation exception
     */
    private ModelControllerClient createControllerClient(final ControllerClientConfig controllerClientConfig)
            throws ControllerOperationException {
        ModelControllerClient controllerClient = null;
        try {
            final CallbackHandler authCallbackHandler = getAuthCallbackHandler(controllerClientConfig);
            final ModelControllerClientConfiguration controllerConfig = ClientConfigurationImpl.create(
                    controllerClientConfig.getHost(), controllerClientConfig.getPort(), authCallbackHandler,
                    controllerClientConfig.getSaslOptions());
            controllerClient = ModelControllerClient.Factory.create(controllerConfig);
        } catch (UnknownHostException uhe) {
            throw new ControllerOperationException(
                    "Tried establishing connection with JBoss controller process. Unable to connect to host: "
                            + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort(), uhe);
        }
        return controllerClient;
    }

    /**
     * Gets the authentication callback handler.
     * 
     * @param controllerClient the controller client
     * @return the authentication callback handler
     */
    private CallbackHandler getAuthCallbackHandler(final ControllerClientConfig controllerClient) {
        return new AuthenticationCallbackHandler(controllerClient.getUserName(), controllerClient.getPassword());
    }

    /**
     * Checks if is local IP address.
     * 
     * @param address the address
     * @return true, if is local IP address
     */
    @SuppressWarnings("unused")
    private static boolean isLocalIpAddress(final InetAddress address) {
        // Check if the address is a valid special local or loop back
        if (address.isAnyLocalAddress() || address.isLoopbackAddress())
            return true;

        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(address) != null;
        } catch (SocketException se) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#isDatasourceExists(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String)
     */
    public boolean isDatasourceExists(final ControllerClientConfig controllerClientConfig, final String dataSourceName)
            throws ControllerOperationException {
        return isDatasourceExists(controllerClientConfig, dataSourceName, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#isDatasourceExists(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String, java.lang.String)
     */
    public boolean isDatasourceExists(final ControllerClientConfig controllerClientConfig, final String dataSourceName,
            final String serverProfileName) throws ControllerOperationException {
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(OPERATION_READ_RESOURCE);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(GENERAL_PROPERTY_RECURSIVE).set(false);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);

        final ModelControllerClient controllerClient = createControllerClient(controllerClientConfig);
        ModelNode responce = null;
        logger.info("Checking if datasource '{}' exists...", dataSourceName);
        try {
            responce = controllerClient.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException(
                    "An error occurred while executing operation on JBoss controller to check if datasource '" + dataSourceName
                            + "' exists", ioe);
        } finally {
            try {
                controllerClient.close();
            } catch (IOException ioe) {
                logger.error(
                        "An error occurred while closing JBoss Controller connection with host {} at port {} during the process of checking if '"
                                + dataSourceName + "' exists", ioe);
            }
        }
        final ModelNode datasources = responce.get(ClientConstants.RESULT).get(ADDRESS_DATASOURCE);
        if (datasources.isDefined()) {
            for (final ModelNode dataSource : datasources.asList()) {
                final String existingSourceName = dataSource.asProperty().getName();
                if (existingSourceName.equals(dataSourceName)) {
                    logger.info("Datasource '{}' exists in datasource subsystem!", dataSourceName);
                    return true;
                }
            }
        } else {
            throw new ControllerOperationException(
                    "A subsystem undefined response status recieved while checking if datasource '" + dataSourceName
                            + "' exists. Most probably the " + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
        }
        logger.info("Datasource '{}' does not exist in datasource subsystem!", dataSourceName);
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#enableDataSource(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String, java.lang.String[])
     */
    public void enableDataSource(final ControllerClientConfig controllerClientConfig, final String datasourceName,
            final String... serverProfileNames) throws ControllerOperationException {
        if (serverProfileNames != null && serverProfileNames.length > 0) {
            for (final String serverProfile : serverProfileNames) {
                enableDataSource(controllerClientConfig, datasourceName, serverProfile);
            }
        } else {
            enableDataSource(controllerClientConfig, datasourceName, "");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#enableDataSource(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String, java.lang.String)
     */
    private void enableDataSource(final ControllerClientConfig controllerClientConfig, final String datasourceName,
            final String serverProfileName) throws ControllerOperationException {
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(OPERATION_ENABLE);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_DATASOURCE, datasourceName);
        ModelControllerClient client = createControllerClient(controllerClientConfig);
        ModelNode response = null;
        try {
            logger.info("Enabling datasource '{}' ...", datasourceName);
            response = client.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException(
                    "An error occurred while executing operation on JBoss controller to enable datasource '" + datasourceName
                            + "'", ioe);
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                logger.error(
                        "An error occurred while closing JBoss Controller connection with host {} at port {} during the process of enabling datasource '"
                                + datasourceName + "'", ioe);
            }
        }
        if (!isOperationSuccess(response)) {
            if (!response.isDefined()) {
                throw new ControllerOperationException(
                        "A subsystem undefined response status recieved while enabling datasource '" + datasourceName
                                + "'. Most probably the " + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
            }
            throw new ControllerOperationException("An error thrown from JBoss controller while enabling datasource:'"
                    + datasourceName + "'.\n" + response.get(ClientConstants.FAILURE_DESCRIPTION).asString());

        }
        logger.info("Datasource '{}' enabled successfully!", datasourceName);
    }

    /**
     * Checks if the operation executed successfully.
     * 
     * @param response the operation response
     * @return true, if the operation is success
     */
    private boolean isOperationSuccess(final ModelNode response) {
        if (!response.isDefined()) {
            return false;
        }
        return ClientConstants.SUCCESS.equals(response.get(ClientConstants.OUTCOME).asString());
    }
}
