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

import static uk.co.techblue.jboss.controller.ControllerConstants.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

// TODO: Auto-generated Javadoc
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
            final JndiDataSource dataSource = new JndiDataSource("java:/mysql-testjboss7", "jdbc:mysql://localhost:3306/test",
                    "com.mysql", "root", "root");
            dataSource.setMaxPoolSize(10);
            dataSource.setPoolPrefill(true);
            final ControllerClientConfig clientConfig = new ControllerClientConfig("127.0.0.1");
//             clientConfig.setUserName("ajay");
//             clientConfig.setPassword("ajay");
//            List<ModelNode> dataSources = new JBoss7ControllerOpeartionExecutor().getDatasources(clientConfig, "", DatasourceStatus.ALL);
//            for (ModelNode dataSource : dataSources) {
//                System.out.println(dataSource.asProperty().getName());
//            }
//             new JBoss7ControllerOpeartionExecutor().disableDataSource(clientConfig, "java:/mysql-testjboss7","");
             new JBoss7ControllerOpeartionExecutor().removeDatasource(clientConfig, "java:/mysql-testjboss7","");
//             new JBoss7ControllerOpeartionExecutor().createDatasource(clientConfig, dataSource, true);
//             new JBoss7ControllerOpeartionExecutor().isDatasourceExists(clientConfig, dataSource.getJndiName());
//             new JBoss7ControllerOpeartionExecutor().createDatasource(clientConfig, dataSource, true, "ha");
//             new JBoss7ControllerOpeartionExecutor().isDatasourceExists(clientConfig, dataSource.getJndiName(), "full-ha");
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
    /**
     * Creates the datasource.
     * 
     * @param controllerClientConfig the controller client config
     * @param dataSource the data source
     * @param enable the enable
     * @param serverProfileName the server profile name
     * @throws ControllerOperationException the controller operation exception
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
                        "An error occurred when closing JBoss Controller connection with host "
                                + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort()
                                + " while adding datasource '" + jndiName + "'", ioe);
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

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#removeDatasource(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, uk.co.techblue.jboss.controller.vo.JndiDataSource, java.lang.String[])
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
     * @param datasourceName the datasource name
     * @param serverProfileName the server profile name
     * @throws ControllerOperationException the controller operation exception
     */
    private void removeDatasource(final ControllerClientConfig controllerClientConfig, final String datasourceName,
            final String serverProfileName) throws ControllerOperationException {
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(ClientConstants.DEPLOYMENT_REMOVE_OPERATION);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_DATASOURCE, datasourceName);
        final ModelControllerClient client = createControllerClient(controllerClientConfig);
        ModelNode response = null;
        try {
            logger.info("Removing datasource '{}' ...", datasourceName);
            response = client.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException("An error occurred while removing datatsource '" + datasourceName
                    + "' from JBoss model controller", ioe);
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                logger.error(
                        "An error occurred when closing JBoss Controller connection with host "
                                + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort()
                                + " while removing datasource '" + datasourceName + "'", ioe);
            }
        }
        if (!isOperationSuccess(response)) {
            if (!response.isDefined()) {
                throw new ControllerOperationException(
                        "A subsystem undefined response status recieved while removing datasource '" + datasourceName
                                + "'. Most probably the " + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
            }
            logger.error("Operation rolled back:" + response.get(RESPONSE_PROPERTY_ROLLEDBACK));
            throw new ControllerOperationException("An error occurred while removing datasource '" + datasourceName + ".\n"
                    + response.get(ClientConstants.FAILURE_DESCRIPTION).asString());
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

    /**
     * Sets the property if not null.
     * 
     * @param request the request
     * @param propertyName the property name
     * @param propertyValue the property value
     */
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
        logger.info("Checking if datasource '{}' exists...", dataSourceName);
        final List<ModelNode> datasources = getDatasources(controllerClientConfig, serverProfileName, DatasourceStatus.ALL);
        if (datasources != null && !datasources.isEmpty()) {
            for (final ModelNode dataSource : datasources) {
                final String existingSourceName = dataSource.asProperty().getName();
                if (existingSourceName.equals(dataSourceName)) {
                    logger.info("Datasource '{}' exists in datasource subsystem!" + dataSourceName);
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
    /**
     * Enable data source.
     * 
     * @param controllerClientConfig the controller client config
     * @param datasourceName the datasource name
     * @param serverProfileName the server profile name
     * @throws ControllerOperationException the controller operation exception
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
                        "An error occurred while closing JBoss Controller connection with host "
                                + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort()
                                + " during the process of enabling datasource '" + datasourceName + "'", ioe);
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

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#getDatasources(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String, uk.co.techblue.jboss.controller.ControllerOperationExecutor.DatasourceStatus)
     */
    @Override
    public List<ModelNode> getDatasources(final ControllerClientConfig controllerClientConfig, final String serverProfileName,
            final DatasourceStatus datasourceStatus) throws ControllerOperationException {

        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(OPERATION_READ_RESOURCE);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(GENERAL_PROPERTY_RECURSIVE).set(false);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);

        final ModelControllerClient controllerClient = createControllerClient(controllerClientConfig);
        ModelNode response = null;
        try {
            response = controllerClient.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException(
                    "An error occurred while executing operation on JBoss controller to get the datasources", ioe);
        } finally {
            try {
                controllerClient.close();
            } catch (IOException ioe) {
                logger.error("An error occurred while closing JBoss Controller client connection with host "
                        + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort()
                        + " during the process of getting all the datasources", ioe);
            }
        }
        if (!isOperationSuccess(response)) {
            if (!response.isDefined()) {
                throw new ControllerOperationException(
                        "A subsystem undefined response status recieved while getting datasources. Most probably the "
                                + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
            }
            throw new ControllerOperationException("An error thrown from JBoss controller while getting datasources.\n"
                    + response.get(ClientConstants.FAILURE_DESCRIPTION).asString());

        }
        final ModelNode datasources = response.get(ClientConstants.RESULT).get(ADDRESS_DATASOURCE);
        if (datasources.isDefined()) {
            if (datasourceStatus == DatasourceStatus.ALL) {
                return datasources.asList();
            } else {
                return getFilteredDataSources(controllerClientConfig, datasourceStatus, datasources.asList());
            }
        } else {
            throw new ControllerOperationException(
                    "A subsystem undefined response status recieved while getting datasources. Most probably the "
                            + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
        }
    }

    /**
     * Gets the filtered data sources.
     * 
     * @param controllerClientConfig the controller client config
     * @param datasourceStatus the datasource status
     * @param datasourceList the datasource list
     * @return the filtered data sources
     * @throws ControllerOperationException the controller operation exception
     */
    private List<ModelNode> getFilteredDataSources(final ControllerClientConfig controllerClientConfig,
            final DatasourceStatus datasourceStatus, final List<ModelNode> datasourceList) throws ControllerOperationException {

        List<ModelNode> datasources = null;
        if (datasourceList == null || datasourceList.isEmpty()) {
            return datasources;
        }
        datasources = new ArrayList<ModelNode>();
        for (ModelNode datasource : datasourceList) {
            final boolean enabled = isDatasourceEnabled(controllerClientConfig, "", datasource.asProperty().getName());
            if (datasourceStatus == DatasourceStatus.ENABLED && enabled) {
                datasources.add(datasource);
            } else if (datasourceStatus == DatasourceStatus.DISABLED && !enabled) {
                datasources.add(datasource);
            }
        }
        return datasources;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#isDatasourceEnabled(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String, java.lang.String)
     */
    public boolean isDatasourceEnabled(final ControllerClientConfig controllerClientConfig, final String serverProfileName,
            final String datasource) throws ControllerOperationException {
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(OPERATION_READ_ATTRIBUTE);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(GENERAL_PROPERTY_RECURSIVE).set(false);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_DATASOURCE, datasource);
        request.get(ClientConstants.NAME).set(ATTRIBUTE_ENABLED);
        final ModelControllerClient controllerClient = createControllerClient(controllerClientConfig);
        ModelNode response = null;
        try {
            response = controllerClient.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException(
                    "An error occurred while executing operation on JBoss controller to get the datasource status", ioe);
        } finally {
            try {
                controllerClient.close();
            } catch (IOException ioe) {
                logger.error("An error occurred while closing JBoss Controller client connection with host "
                        + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort()
                        + " during the process of getting the status of datasource '" + datasource + "'", ioe);
            }
        }
        if (!isOperationSuccess(response)) {
            if (!response.isDefined()) {
                throw new ControllerOperationException(
                        "A subsystem undefined response status recieved while getting the status of datasource '" + datasource
                                + "'. Most probably the " + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
            }
            throw new ControllerOperationException(
                    "An error thrown from JBoss controller while getting the status of datasource '" + datasource + "'.\n"
                            + response.get(ClientConstants.FAILURE_DESCRIPTION).asString());

        }
        return response.get(ClientConstants.RESULT).asBoolean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#disableDataSource(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.lang.String, java.lang.String[])
     */
    @Override
    public void disableDataSource(ControllerClientConfig controllerClientConfig, String datasourceName,
            String... serverProfileNames) throws ControllerOperationException {
        if (serverProfileNames != null && serverProfileNames.length > 0) {
            for (final String serverProfile : serverProfileNames) {
                disableDataSource(controllerClientConfig, datasourceName, serverProfile);
            }
        } else {
            disableDataSource(controllerClientConfig, datasourceName, "");
        }
    }

    /**
     * Disable data source.
     * 
     * @param controllerClientConfig the controller client config
     * @param datasourceName the datasource name
     * @param serverProfileName the server profile name
     * @throws ControllerOperationException the controller operation exception
     */
    private void disableDataSource(final ControllerClientConfig controllerClientConfig, final String datasourceName,
            final String serverProfileName) throws ControllerOperationException {

        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set(OPERATION_DISABLE);
        if (StringUtils.isNotBlank(serverProfileName)) {
            request.get(ClientConstants.OP_ADDR).add(ADDRESS_PROFILE, serverProfileName);
        }
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_SUBSYSTEM, DATASOURCE_SUBSYSTEM);
        request.get(ClientConstants.OP_ADDR).add(ADDRESS_DATASOURCE, datasourceName);
        ModelControllerClient client = createControllerClient(controllerClientConfig);
        ModelNode response = null;
        try {
            logger.info("Disabling datasource '{}' ...", datasourceName);
            response = client.execute(new OperationBuilder(request).build());
        } catch (IOException ioe) {
            throw new ControllerOperationException(
                    "An error occurred while executing operation on JBoss controller to disable datasource '" + datasourceName
                            + "'", ioe);
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                logger.error(
                        "An error occurred while closing JBoss Controller connection with host "
                                + controllerClientConfig.getHost() + " at port " + controllerClientConfig.getPort()
                                + " during the process of disabling datasource '" + datasourceName + "'", ioe);
            }
        }
        if (!isOperationSuccess(response)) {
            if (!response.isDefined()) {
                throw new ControllerOperationException(
                        "A subsystem undefined response status recieved while disabling datasource '" + datasourceName
                                + "'. Most probably the " + DATASOURCE_SUBSYSTEM + " subsystem is not defined.");
            }
            throw new ControllerOperationException("An error thrown from JBoss controller while disabling datasource:'"
                    + datasourceName + "'.\n" + response.get(ClientConstants.FAILURE_DESCRIPTION).asString());

        }
        logger.info("Datasource '{}' disabled successfully!", datasourceName);

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#enableDataSources(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.util.List, java.lang.String[])
     */
    @Override
    public void enableDataSources(final ControllerClientConfig controllerClientConfig, final List<String> dataSourceNames,
            final String... serverProfileNames) throws ControllerOperationException {
        if (dataSourceNames == null || dataSourceNames.isEmpty()) {
            throw new IllegalArgumentException("Datasource list cannot be blank or null.");
        }

        for (String dataSourceName : dataSourceNames) {
            enableDataSource(controllerClientConfig, dataSourceName, serverProfileNames);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.techblue.jboss.controller.ControllerOperationExecutor#disableDataSources(uk.co.techblue.jboss.controller.vo.
     * ControllerClientConfig, java.util.List, java.lang.String[])
     */
    @Override
    public void disableDataSources(final ControllerClientConfig controllerClientConfig, final List<String> dataSourceNames,
            final String... serverProfileNames) throws ControllerOperationException {
        if (dataSourceNames == null || dataSourceNames.isEmpty()) {
            throw new IllegalArgumentException("Datasource list cannot be blank or null.");
        }
        for (String dataSourceName : dataSourceNames) {
            disableDataSource(controllerClientConfig, dataSourceName, serverProfileNames);
        }

    }
}
