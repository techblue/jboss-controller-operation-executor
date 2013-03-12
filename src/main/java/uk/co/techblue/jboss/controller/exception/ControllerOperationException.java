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
package uk.co.techblue.jboss.controller.exception;

/**
 * The ControllerOperationException class.
 * 
 * @author <a href="mailto:ajay.deshwal@techblue.co.uk">Ajay Deshwal</a>
 */
public class ControllerOperationException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1802603991955512461L;

    /**
     * Instantiates a new controller operation exception.
     * 
     * @param message the message
     * @param cause the cause
     */
    public ControllerOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new controller operation exception.
     * 
     * @param message the message
     */
    public ControllerOperationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new controller operation exception.
     * 
     * @param cause the cause
     */
    public ControllerOperationException(Throwable cause) {
        super(cause);
    }

}
