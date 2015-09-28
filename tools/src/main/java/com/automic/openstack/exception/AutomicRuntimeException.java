/**
 *
 */
package com.automic.openstack.exception;

/**
 * @author sumitsamson
 *
 */
/**
 * This exception is used to throw {@link RuntimeException}
* */
public class AutomicRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 8908847435912088181L;

    /**
     * @param message
     */
    public AutomicRuntimeException(String message) {
        super(message);

    }

}
