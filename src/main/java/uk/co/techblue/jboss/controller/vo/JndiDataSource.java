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

import java.util.Properties;

import uk.co.techblue.jboss.util.StringUtils;

/**
 * A java bean class to hold Jndi DataSource configuration.
 * 
 * @author <a href="mailto:ajay.deshwal@techblue.co.uk">Ajay Deshwal</a>
 */
public class JndiDataSource {

    /**
     * The Enum to denote Transaction Isolation types.
     */
    public enum TransactionIsolation {
        TRANSACTION_READ_UNCOMMITTED,
        TRANSACTION_READ_COMMITTED,
        TRANSACTION_REPEATABLE_READ,
        TRANSACTION_SERIALIZABLE,
        TRANSACTION_NONE
    }

    /** The name. */
    private final String name;

    /** The jndi name. */
    private final String jndiName;

    /** The use java context. */
    private boolean useJavaContext = true;

    /** The pool name. */
    private String poolName;

    /** The connection url. */
    private final String connectionURL;

    /** The driver name. */
    private final String driverName;

    /** The user name. */
    private final String userName;

    /** The password. */
    private final String password;

    /** The transaction isolation. */
    private TransactionIsolation transactionIsolation = TransactionIsolation.TRANSACTION_READ_UNCOMMITTED;

    /** The min pool size. */
    private int minPoolSize = 1;

    /** The max pool size. */
    private int maxPoolSize = 2;

    /** The pool prefill. */
    private boolean poolPrefill;

    /** The share prepared statements. */
    private boolean sharePreparedStatements;

    /** The statement cache size. */
    private int statementCacheSize;

    /** The use jta. */
    private boolean useJTA = true;

    /** The use cache connection manager. */
    private boolean useCacheConnectionManager = true;

    /** The new connection sql. */
    private String newConnectionSQL;

    /** The security domain. */
    private String securityDomain;

    /** The connection properties. */
    private Properties connectionProperties;

    /** The pool strict minimum. */
    private boolean poolStrictMinimum;

    /** The check valid sql. */
    private String checkValidSQL;

    /** The validate on match. */
    private boolean validateOnMatch;

    /** The background validation. */
    private boolean backgroundValidation;

    /** The background validation millisec. */
    private long backgroundValidationMillisec;

    /** The stale connection checker class name. */
    private String staleConnectionCheckerClassName;

    /** The valid connection checker class name. */
    private String validConnectionCheckerClassName = "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker";

    /** The exception sorter class name. */
    private String exceptionSorterClassName = "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter";

    /**
     * Instantiates a new jndi data source.
     * 
     * @param jndiName the jndi name
     * @param connectionURL the connection url
     * @param driverName the driver name
     * @param userName the user name
     * @param password the password
     */
    public JndiDataSource(String jndiName, String connectionURL, String driverName, String userName, String password) {
        this(jndiName, jndiName, connectionURL, driverName, userName, password);
    }

    /**
     * Instantiates a new JNDI data source.
     * 
     * @param name the datasource name
     * @param jndiName the datasource JNDI name
     * @param connectionURL The JDBC driver connection URL
     * @param driverName Defines the JDBC driver the datasource should use. It is a symbolic name matching the the name of
     *        installed driver. In case the driver is deployed as jar, the name is the name of deployment unit
     * @param userName Specify the user name used when creating a new connection
     * @param password Specifies the password used when creating a new connection
     */
    public JndiDataSource(String name, String jndiName, String connectionURL, String driverName, String userName,
            String password) {
        if (!jndiName.startsWith("java:/") && !jndiName.startsWith("java:jboss")) {
            jndiName = "java:/" + jndiName;
        }
        if (StringUtils.isBlank(name)) {
            name = jndiName.replace("java:/", "").replace("java:jboss", "");
        }
        this.name = name;
        this.jndiName = jndiName;
        this.connectionURL = connectionURL;
        this.driverName = driverName;
        this.userName = userName;
        this.password = password;
        this.poolName = jndiName.replace("java:/", "") + "-pool";
    }

    /**
     * Gets the datasource name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the JNDI name for the datasource.
     * 
     * @return the jndi name
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * Whether use java context.
     * 
     * @return true, if is use java context
     */
    public boolean isUseJavaContext() {
        return useJavaContext;
    }

    /**
     * Sets whether to use java context.
     * 
     * @param useJavaContext the new use java context
     */
    public void setUseJavaContext(boolean useJavaContext) {
        this.useJavaContext = useJavaContext;
    }

    /**
     * Gets the connection pool name.
     * 
     * @return the pool name
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * Sets the connection pool name.
     * 
     * @param poolName the new pool name
     */
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * Gets the JDBC driver connection URL.
     * 
     * @return the connection url
     */
    public String getConnectionURL() {
        return connectionURL;
    }

    /**
     * Gets the driver name.
     * 
     * @return the driver name
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Gets the user name.
     * 
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the transaction isolation.
     * 
     * @return the transaction isolation
     */
    public TransactionIsolation getTransactionIsolation() {
        return transactionIsolation;
    }

    /**
     * Sets the {@link java.sql.Connection} transaction isolation level. Valid values are: TRANSACTION_READ_UNCOMMITTED,
     * TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE and TRANSACTION_NONE
     * 
     * @param transactionIsolation the new transaction isolation
     */
    public void setTransactionIsolation(TransactionIsolation transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    /**
     * Gets the minimum number of connections for a pool
     * 
     * @return the min pool size
     */
    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * Specifies the minimum number of connections for a pool
     * 
     * @param minPoolSize the new min pool size
     */
    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    /**
     * Gets the maximum number of connections for a pool. No more connections will be created in each sub-pool.
     * 
     * @return the max pool size
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Specifies the maximum number of connections for a pool. No more connections will be created in each sub-pool
     * 
     * @param maxPoolSize the new max pool size
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Should the pool be prefilled. Changing this value can be done only on disabled datasource, requires a server restart
     * otherwise.
     * 
     * @return true, if is pool prefill
     */
    public boolean isPoolPrefill() {
        return poolPrefill;
    }

    /**
     * Should the pool be prefilled. Changing this value can be done only on disabled datasource, requires a server restart
     * otherwise.
     * 
     * @param poolPrefill the new pool prefill
     */
    public void setPoolPrefill(boolean poolPrefill) {
        this.poolPrefill = poolPrefill;
    }

    /**
     * Gets the fully qualified class name of a {@link org.jboss.jca.adapters.jdbc.ValidConnectionChecker} implementation that
     * provides an isValidConnection(Connection) method to validate a connection. If an exception is returned that means the
     * connection is invalid. This overrides the check-valid-connection-sql element
     * 
     * @return the valid connection checker class name
     */
    public String getValidConnectionCheckerClassName() {
        return validConnectionCheckerClassName;
    }

    /**
     * Specifies the fully qualified class name of a {@link org.jboss.jca.adapters.jdbc.ValidConnectionChecker} implementation
     * that provides an isValidConnection(Connection) method to validate a connection. If an exception is returned that means
     * the connection is invalid. This overrides the check-valid-connection-sql element.
     * 
     * @param validConnectionCheckerClassName the new valid connection checker class name
     */
    public void setValidConnectionCheckerClassName(String validConnectionCheckerClassName) {
        this.validConnectionCheckerClassName = validConnectionCheckerClassName;
    }

    /**
     * Gets the class name of a {@link org.jboss.jca.adapters.jdbc.ExceptionSorter} implementation that provides an
     * isExceptionFatal(SQLException) method to validate if an exception should broadcast an error.
     * 
     * @return the exception sorter class name
     */
    public String getExceptionSorterClassName() {
        return exceptionSorterClassName;
    }

    /**
     * Sets the class name of a {@link org.jboss.jca.adapters.jdbc.ExceptionSorter} implementation that provides an
     * isExceptionFatal(SQLException) method to validate if an exception should broadcast an error.
     * 
     * @param exceptionSorterClassName the new exception sorter class name
     */
    public void setExceptionSorterClassName(String exceptionSorterClassName) {
        this.exceptionSorterClassName = exceptionSorterClassName;
    }

    /**
     * Whether to share prepared statements, i.e. whether asking for same statement twice without closing uses the same
     * underlying prepared statement.
     * 
     * @return true, if is share prepared statements
     */
    public boolean isSharePreparedStatements() {
        return sharePreparedStatements;
    }

    /**
     * Specifies Whether to share prepared statements, i.e. whether asking for same statement twice without closing uses the
     * same underlying prepared statement.
     * 
     * @param sharePreparedStatements the new share prepared statements
     */
    public void setSharePreparedStatements(boolean sharePreparedStatements) {
        this.sharePreparedStatements = sharePreparedStatements;
    }

    /**
     * Gets the number of prepared statements per connection in an LRU cache
     * 
     * @return the statement cache size
     */
    public int getStatementCacheSize() {
        return statementCacheSize;
    }

    /**
     * Sets the number of prepared statements per connection in an LRU cache
     * 
     * @param statementCacheSize the new statement cache size
     */
    public void setStatementCacheSize(int statementCacheSize) {
        this.statementCacheSize = statementCacheSize;
    }

    /**
     * Whether to enable JTA integration.
     * 
     * @return true, if is use jta
     */
    public boolean isUseJTA() {
        return useJTA;
    }

    /**
     * Sets whether to enable JTA integration.
     * 
     * @param useJTA the new use jta
     */
    public void setUseJTA(boolean useJTA) {
        this.useJTA = useJTA;
    }

    /**
     * Whether to use of a cached connection manager.
     * 
     * @return true, if is use cache connection manager
     */
    public boolean isUseCacheConnectionManager() {
        return useCacheConnectionManager;
    }

    /**
     * Sets Whether to use of a cached connection manager.
     * 
     * @param useCacheConnectionManager the new use cache connection manager
     */
    public void setUseCacheConnectionManager(boolean useCacheConnectionManager) {
        this.useCacheConnectionManager = useCacheConnectionManager;
    }

    /**
     * Gets an SQL statement to execute whenever a connection is added to the connection pool
     * 
     * @return the new connection sql
     */
    public String getNewConnectionSQL() {
        return newConnectionSQL;
    }

    /**
     * Specifies an SQL statement to execute whenever a connection is added to the connection pool.
     * 
     * @param newConnectionSQL the new new connection sql
     */
    public void setNewConnectionSQL(String newConnectionSQL) {
        this.newConnectionSQL = newConnectionSQL;
    }

    /**
     * Gets the security domain which defines the {@link javax.security.auth.Subject} that are used to distinguish connections
     * in the pool
     * 
     * @return the security domain
     */
    public String getSecurityDomain() {
        return securityDomain;
    }

    /**
     * Specifies the security domain which defines the {@link javax.security.auth.Subject} that are used to distinguish
     * connections in the pool
     * 
     * @param securityDomain the new security domain
     */
    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

    /**
     * Gets the JDBC connection properties.
     * 
     * @return the connection properties
     */
    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    /**
     * Sets the JDBC connection properties.
     * 
     * @param connectionProperties the new connection properties
     */
    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    /**
     * Checks if the minimum pool size should be considered strictly.
     * 
     * @return true, if is pool strict minimum
     */
    public boolean isPoolStrictMinimum() {
        return poolStrictMinimum;
    }

    /**
     * Specifies if the minimum pool size should be considered strictly.
     * 
     * @param poolStrictMinimum the new pool strict minimum
     */
    public void setPoolStrictMinimum(boolean poolStrictMinimum) {
        this.poolStrictMinimum = poolStrictMinimum;
    }

    /**
     * Gets the SQL statement to check validity of a pool connection. This may be called when managed connection is obtained
     * from the pool.
     * 
     * @return the check valid sql
     */
    public String getCheckValidSQL() {
        return checkValidSQL;
    }

    /**
     * Specify an SQL statement to check validity of a pool connection. This may be called when managed connection is obtained
     * from the pool.
     * 
     * @param checkValidSQL the new check valid sql
     */
    public void setCheckValidSQL(String checkValidSQL) {
        this.checkValidSQL = checkValidSQL;
    }

    /**
     * Checks if connection validation should be done when a connection factory attempts to match a managed connection. This is
     * typically exclusive to the use of background validation
     * 
     * @return true, if is validate on match
     */
    public boolean isValidateOnMatch() {
        return validateOnMatch;
    }

    /**
     * Specifies if connection validation should be done when a connection factory attempts to match a managed connection. This
     * is typically exclusive to the use of background validation
     * 
     * @param validateOnMatch the new validate on match
     */
    public void setValidateOnMatch(boolean validateOnMatch) {
        this.validateOnMatch = validateOnMatch;
    }

    /**
     * Checks whether connections should be validated on a background thread versus being validated prior to use. Changing this
     * value can be done only on disabled datasource, requires a server restart otherwise.
     * 
     * @return true, if is background validation
     */
    public boolean isBackgroundValidation() {
        return backgroundValidation;
    }

    /**
     * Specify that connections should be validated on a background thread versus being validated prior to use. Changing this
     * value can be done only on disabled datasource, requires a server restart otherwise.
     * 
     * @param backgroundValidation the new background validation
     */
    public void setBackgroundValidation(boolean backgroundValidation) {
        this.backgroundValidation = backgroundValidation;
    }

    /**
     * Gets the amount of time, in milliseconds, that background validation will run. Changing this value can be done only on
     * disabled datasource, requires a server restart otherwise.
     * 
     * @return the background validation time in millisec
     */
    public long getBackgroundValidationMillisec() {
        return backgroundValidationMillisec;
    }

    /**
     * Specifies the amount of time, in milliseconds, that background validation will run. Changing this value can be done only
     * on disabled datasource, requires a server restart otherwise.
     * 
     * @param backgroundValidationMillisec the new background validation time in millisec
     */
    public void setBackgroundValidationMillisec(long backgroundValidationMillisec) {
        this.backgroundValidationMillisec = backgroundValidationMillisec;
    }

    /**
     * Gets the fully qualified name of the class which implements {@link org.jboss.jca.adapters.jdbc.StaleConnectionChecker}
     * that provides an isStaleConnection(SQLException) method which if returns true will wrap the exception in an
     * {@link org.jboss.jca.adapters.jdbc.StaleConnectionException}.
     * 
     * @return the stale connection checker class name
     */
    public String getStaleConnectionCheckerClassName() {
        return staleConnectionCheckerClassName;
    }

    /**
     * Sets the fully qualified name of the class which implements {@link org.jboss.jca.adapters.jdbc.StaleConnectionChecker}
     * that provides an isStaleConnection(SQLException) method which if returns true will wrap the exception in an
     * {@link org.jboss.jca.adapters.jdbc.StaleConnectionException}.
     * 
     * @param staleConnectionCheckerClassName the new stale connection checker class name
     */
    public void setStaleConnectionCheckerClassName(String staleConnectionCheckerClassName) {
        this.staleConnectionCheckerClassName = staleConnectionCheckerClassName;
    }

}
