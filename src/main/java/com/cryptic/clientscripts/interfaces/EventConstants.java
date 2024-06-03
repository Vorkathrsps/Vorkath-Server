package com.cryptic.clientscripts.interfaces;

public enum EventConstants {

    /**
     * Enabled the component to be a "pause_button", clicking it sends {@link DialogueReader}.
     */
    PAUSE(1),

    ClickOp1(1 << 1),

    ClickOp2(1 << 2),

    ClickOp3(1 << 3),

    ClickOp4(1 << 4),

    ClickOp5(1 << 5),

    ClickOp6(1 << 6),

    ClickOp7(1 << 7),

    ClickOp8(1 << 8),

    ClickOp9(1 << 9),

    ClickOp10(1 << 10),

    UseOnGroundItem(1 << 11),

    UseOnNpc(1 << 12),

    UseOnObject(1 << 13),

    UseOnPlayer(1 << 14),

    UseOnInventory(1 << 15),

    UseOnComponent(1 << 16),

    DRAG_DEPTH1(1 << 17),

    DRAG_DEPTH2(2 << 17),

    DRAG_DEPTH3(3 << 17),

    DRAG_DEPTH4(4 << 17),

    DRAG_DEPTH5(5 << 17),

    DRAG_DEPTH6(6 << 17),

    DRAG_DEPTH7(7 << 17),

    DragTargetable(1 << 20),

    ComponentTargetable(1 << 21);

    /**
     * The integer representation of the event.
     */
    private final int flag;

    EventConstants(final int flag) {
        this.flag = flag;
    }

    /**
     * Gets the flag for the event.
     */
    public int getFlag() {
        return flag;
    }

}
