package com.automic.openstack.constants;

/**
 * Enum that defines constants which are placeholder for actions. When an implementation of {@link AbstractAction} is
 * created we also create a constant in this enum. Mapping is defined in {@link ActionFactory}
 */
public enum Action {

    GET_AUTH_TOKEN, LIST_SERVERS, GET_SERVER_DETAILS, DELETE_FILE, CHANGE_SERVER_STATE, CREATE_SERVER_SNAPSHOT, CREATE_SERVERS, LIST_SNAPSHOTS, DELETE_SERVER,FLOATING_IP;

    public static String getActionNames() {
        Action[] actions = Action.values();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < actions.length; i++) {
            sb.append(actions[i].name());
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

}
