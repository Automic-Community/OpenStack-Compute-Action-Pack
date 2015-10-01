package com.automic.openstack.constants;

/**
 * Enum that defines constants which are placeholder for actions. When an implementation of {@link AbstractAction} is
 * created we also create a constant in this enum. Mapping is defined in {@link ActionFactory}
 */
public enum Action {

    GET_TOKEN;

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
