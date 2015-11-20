package com.automic.openstack.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Action;
import com.automic.openstack.exception.AutomicException;

/**
 * Factory class to create instances of implementations of {@link AbstractAction}. This class will create the instances
 * based on {@link Action} parameter. It throws an Exception if no matching implementation could be found.
 */
public final class ActionFactory {

    private static final Logger LOGGER = LogManager.getLogger(AbstractAction.class);

    private ActionFactory() {
    }

    /**
     * Method to return instance of implementation of {@link AbstractAction} based on value of enum {@link Action}
     * passed.
     * 
     * @param enumAction
     * @return an implementation of {@link AbstractAction}
     * @throws AutomicException
     *             if no matching implementation could be found
     */
    public static AbstractAction getAction(Action enumAction) throws AutomicException {

        AbstractAction action = null;

        switch (enumAction) {
            case GET_AUTH_TOKEN:
                action = new GetTokenAction();
                break;
            case LIST_SERVERS:
                action = new ListServersAction();
                break;
            case GET_SERVER_DETAILS:
                action = new GetServerDetailsAction();
                break;
            case DELETE_FILE:
                action = new DeleteFileAction();
                break;
            case CHANGE_SERVER_STATE:
                action = new ChangeServerStateAction();
                break;
            case CREATE_SERVER_SNAPSHOT:
                action = new CreateServerSnapshotAction();
                break;
            case CREATE_SERVERS:
                action = new CreateServerAction();
                break;
            case LIST_SNAPSHOTS:
                action = new ListSnapshotsAction();
                break;
            case FLOATING_IP:
                action = new AllocateDeallocateFloatingIPAction();
                break;
            case DELETE_SERVER :
                action = new DeleteServerAction();
                break;
            default:
                String msg = "Invalid Action.. Please enter valid action " + Action.getActionNames();
                LOGGER.error(msg);
                throw new AutomicException(msg);
        }
        return action;
    }

}
