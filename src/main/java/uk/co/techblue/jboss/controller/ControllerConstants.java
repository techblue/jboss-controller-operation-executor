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
package uk.co.techblue.jboss.controller;

/**
 * The JBoss AS Controller Constants.
 * 
 * @author <a href="mailto:ajay.deshwal@techblue.co.uk">Ajay Deshwal</a>
 */
public class ControllerConstants {

    public static final String DATASOURCE_SUBSYSTEM = "datasources";

    public static final String OPERATION_READ_RESOURCE = "read-resource";
    public static final String OPERATION_READ_ATTRIBUTE = "read-attribute";
    public static final String OPERATION_ENABLE = "enable";
    public static final String OPERATION_DISABLE = "disable";

    public static final String ADDRESS_PROFILE = "profile";
    public static final String ADDRESS_SUBSYSTEM = "subsystem";
    public static final String ADDRESS_DATASOURCE = "data-source";
    public static final String ATTRIBUTE_ENABLED = "enabled";

    public static final String GENERAL_PROPERTY_RECURSIVE = "recursive";

    public static final String DS_PROPERTY_JNDINAME = "jndi-name";
    public static final String DS_PROPERTY_USEJAVACONTEXT = "use-java-context";
    public static final String DS_PROPERTY_SHARE_PREPARED_STATEMENTS = "share-prepared-statements";
    public static final String DS_PROPERTY_PREPARED_STATEMENTS_CACHE_SIZE = "prepared-statements-cache-size";
    public static final String DS_PROPERTY_POOLNAME = "pool-name";
    public static final String DS_PROPERTY_CONNECTIONURL = "connection-url";
    public static final String DS_PROPERTY_NEWCONNECTIONSQL = "new-connection-sql";
    public static final String DS_PROPERTY_USE_CCM = "use-ccm";
    public static final String DS_PROPERTY_JTA_INTEGRATION = "jta";
    public static final String DS_PROPERTY_DRIVERNAME = "driver-name";
    public static final String DS_PROPERTY_USERNAME = "user-name";
    public static final String DS_PROPERTY_PASSWORD = "password";
    public static final String DS_PROPERTY_SECURITYDOMAIN = "security-domain";
    public static final String DS_PROPERTY_TRANSACTIONISOLATION = "transaction-isolation";
    public static final String DS_PROPERTY_MINPOOLSIZE = "min-pool-size";
    public static final String DS_PROPERTY_MAXPOOLSIZE = "max-pool-size";
    public static final String DS_PROPERTY_POOLPREFILL = "pool-prefill";
    public static final String DS_PROPERTY_POOL_STRICT_MINIMUM = "pool-use-strict-min";
    public static final String DS_PROPERTY_VALID_CONNECTION_SQL = "check-valid-connection-sql";
    public static final String DS_PROPERTY_STALE_CONNCHECKER_CLASSNAME = "stale-connection-checker-class-name";
    public static final String DS_PROPERTY_VALID_CONNCHECKER_CLASSNAME = "valid-connection-checker-class-name";
    public static final String DS_PROPERTY_EXCEPTION_SORTER_CLASSNAME = "exception-sorter-class-name";
    public static final String DS_PROPERTY_BG_VALIDATION = "background-validation";
    public static final String DS_PROPERTY_BG_VALIDATION_MILLIS = "background-validation-millis";
    public static final String DS_PROPERTY_VALIDATE_ON_MATCH = "validate-on-match";

    public static final String RESPONSE_PROPERTY_ROLLEDBACK = "rolled-back";

}
