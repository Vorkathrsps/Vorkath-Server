package com.cryptic.clientscripts.impl.equipment.util;

public class ToggleManager {

    public final boolean[] toggledStates = new boolean[4];
    public final int[] args = new int[4];

    public void toggle(int slot) {
        if (slot < 0 || slot >= toggledStates.length) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        int val = slot == 3 ? 21 : 1;
        toggledStates[slot] = !toggledStates[slot];
        args[slot] = toggledStates[slot] ? val : 0;
    }

    public boolean isToggled(int slot) {
        if (slot < 0 || slot >= toggledStates.length) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        return toggledStates[slot];
    }
}
