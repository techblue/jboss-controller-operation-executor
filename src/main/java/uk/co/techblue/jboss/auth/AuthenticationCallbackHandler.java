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
package uk.co.techblue.jboss.auth;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import uk.co.techblue.jboss.util.StringUtils;

/**
 * The implementation of {@link javax.security.auth.callback.CallbackHandler} for passing to underlying authentication services.
 * 
 * @author <a href="mailto:ajay.deshwal@techblue.co.uk">Ajay Deshwal</a>
 */
public class AuthenticationCallbackHandler implements CallbackHandler {

    /** The realm. */
    private final String realm;

    /** The user name. */
    private final String username;

    /** The password. */
    private final char[] password;

    /**
     * Instantiates a new authentication callback handler.
     * 
     * @param username the user name
     * @param password the password
     * @param realm the authentication realm
     */
    public AuthenticationCallbackHandler(final String username, final char[] password, final String realm) {
        this.username = username;
        this.password = password;
        this.realm = realm;
    }

    /**
     * Instantiates a new authentication callback handler.
     * 
     * @param username the user name
     * @param password the password
     * @param realm the authentication realm
     */
    public AuthenticationCallbackHandler(final String username, final String password, final String realm) {
        this(username, password.toCharArray(), realm);
    }

    /**
     * Instantiates a new authentication callback handler.
     * 
     * @param username the user name
     * @param password the password
     */
    public AuthenticationCallbackHandler(final String username, final String password) {
        this(username, StringUtils.defaultString(password).toCharArray(), null);
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback current : callbacks) {
            if (current instanceof RealmCallback) {
                RealmCallback realmCallback = (RealmCallback) current;
                if (realm == null || realm.trim().length() == 0) {
                    String defaultRealm = realmCallback.getDefaultText();
                    realmCallback.setText(defaultRealm);
                } else {
                    realmCallback.setText(realm);
                }
            } else if (current instanceof NameCallback) {
                NameCallback nameCallback = (NameCallback) current;
                nameCallback.setName(username);
            } else if (current instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) current;
                passwordCallback.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(current);
            }
        }
    }
}
