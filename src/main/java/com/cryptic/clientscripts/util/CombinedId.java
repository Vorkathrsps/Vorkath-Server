package com.cryptic.clientscripts.util;

public class CombinedId {

    public final int combinedId;

    public CombinedId(int combinedId) {
        this.combinedId = combinedId;
    }

    public CombinedId(int interfaceId, int componentId) {
        this((interfaceId & 0xFFFF) << 16 | (componentId & 0xFFFF));
    }

    public int getInterfaceId() {
        int value = (combinedId >>> 16) & 0xFFFF;
        return value == 0xFFFF ? -1 : value;
    }

    public int getComponentId() {
        int value = combinedId & 0xFFFF;
        return value == 0xFFFF ? -1 : value;
    }

    public int component1() {
        return getInterfaceId();
    }

    public int component2() {
        return getComponentId();
    }

    @Override
    public String toString() {
        return "CombinedId(" +
            "interfaceId=" + getInterfaceId() + ", " +
            "componentId=" + getComponentId() +
            ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CombinedId that = (CombinedId) o;
        return combinedId == that.combinedId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(combinedId);
    }
}
