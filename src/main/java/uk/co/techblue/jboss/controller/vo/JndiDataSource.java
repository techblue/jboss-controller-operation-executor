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

public class JndiDataSource {

    public enum TransactionIsolation {
        TRANSACTION_READ_UNCOMMITTED,
        TRANSACTION_READ_COMMITTED,
        TRANSACTION_REPEATABLE_READ,
        TRANSACTION_SERIALIZABLE,
        TRANSACTION_NONE
    }

    private final String name;

    private final String jndiName;

    private boolean useJavaContext = true;

    private String poolName;

    private final String connectionURL;

    private final String driverName;

    private final String userName;

    private final String password;

    private TransactionIsolation transactionIsolation = TransactionIsolation.TRANSACTION_READ_UNCOMMITTED;

    private int minPoolSize = 1;

    private int maxPoolSize = 2;

    private boolean poolPrefill;

    private boolean sharePreparedStatements;

    private int statementCacheSize;

    private boolean useJTA = true;

    private boolean useCacheConnectionManager = true;

    private String newConnectionSQL;

    private String securityDomain;

    private Properties connectionProperties;

    private boolean poolStrictMinimum;

    private String checkValidSQL;

    private boolean validateOnMatch;

    private boolean backgroundValidation;

    private long backgroundValidationMillisec;

    private String staleConnectionCheckerClassName;

    private String validConnectionCheckerClassName = "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker";

    private String exceptionSorterClassName = "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter";

    public JndiDataSource(String jndiName, String connectionURL, String driverName, String userName, String password) {
        this(jndiName, jndiName, connectionURL, driverName, userName, password);
    }

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

    public String getName() {
        return name;
    }

    public String getJndiName() {
        return jndiName;
    }

    public boolean isUseJavaContext() {
        return useJavaContext;
    }

    public void setUseJavaContext(boolean useJavaContext) {
        this.useJavaContext = useJavaContext;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public TransactionIsolation getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(TransactionIsolation transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean isPoolPrefill() {
        return poolPrefill;
    }

    public void setPoolPrefill(boolean poolPrefill) {
        this.poolPrefill = poolPrefill;
    }

    public String getValidConnectionCheckerClassName() {
        return validConnectionCheckerClassName;
    }

    public void setValidConnectionCheckerClassName(String validConnectionCheckerClassName) {
        this.validConnectionCheckerClassName = validConnectionCheckerClassName;
    }

    public String getExceptionSorterClassName() {
        return exceptionSorterClassName;
    }

    public void setExceptionSorterClassName(String exceptionSorterClassName) {
        this.exceptionSorterClassName = exceptionSorterClassName;
    }

    public boolean isSharePreparedStatements() {
        return sharePreparedStatements;
    }

    public void setSharePreparedStatements(boolean sharePreparedStatements) {
        this.sharePreparedStatements = sharePreparedStatements;
    }

    public int getStatementCacheSize() {
        return statementCacheSize;
    }

    public void setStatementCacheSize(int statementCacheSize) {
        this.statementCacheSize = statementCacheSize;
    }

    public boolean isUseJTA() {
        return useJTA;
    }

    public void setUseJTA(boolean useJTA) {
        this.useJTA = useJTA;
    }

    public boolean isUseCacheConnectionManager() {
        return useCacheConnectionManager;
    }

    public void setUseCacheConnectionManager(boolean useCacheConnectionManager) {
        this.useCacheConnectionManager = useCacheConnectionManager;
    }

    public String getNewConnectionSQL() {
        return newConnectionSQL;
    }

    public void setNewConnectionSQL(String newConnectionSQL) {
        this.newConnectionSQL = newConnectionSQL;
    }

    public String getSecurityDomain() {
        return securityDomain;
    }

    public void setSecurityDomain(String securityDomain) {
        this.securityDomain = securityDomain;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public boolean isPoolStrictMinimum() {
        return poolStrictMinimum;
    }

    public void setPoolStrictMinimum(boolean poolStrictMinimum) {
        this.poolStrictMinimum = poolStrictMinimum;
    }

    public String getCheckValidSQL() {
        return checkValidSQL;
    }

    public void setCheckValidSQL(String checkValidSQL) {
        this.checkValidSQL = checkValidSQL;
    }

    public boolean isValidateOnMatch() {
        return validateOnMatch;
    }

    public void setValidateOnMatch(boolean validateOnMatch) {
        this.validateOnMatch = validateOnMatch;
    }

    public boolean isBackgroundValidation() {
        return backgroundValidation;
    }

    public void setBackgroundValidation(boolean backgroundValidation) {
        this.backgroundValidation = backgroundValidation;
    }

    public long getBackgroundValidationMillisec() {
        return backgroundValidationMillisec;
    }

    public void setBackgroundValidationMillisec(long backgroundValidationMillisec) {
        this.backgroundValidationMillisec = backgroundValidationMillisec;
    }

    public String getStaleConnectionCheckerClassName() {
        return staleConnectionCheckerClassName;
    }

    public void setStaleConnectionCheckerClassName(String staleConnectionCheckerClassName) {
        this.staleConnectionCheckerClassName = staleConnectionCheckerClassName;
    }

}
