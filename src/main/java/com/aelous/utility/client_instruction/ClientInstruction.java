package com.aelous.utility.client_instruction;

import com.aelous.model.entity.player.Player;

import java.util.ArrayList;

/**
 * A helper to construct and send a client instruction with the respective arguments. The order of the arguments
 * must respect what the instruction expects.
 * @author Heaven
 */
public class ClientInstruction {

    private final int instructionId;
    private ArrayList<Object> arguments = new ArrayList<>();

    public static ClientInstruction of(int instructionId) {
        return new ClientInstruction(instructionId);
    }

    private ClientInstruction(int id) {
        this.instructionId = id;
    }

    public ClientInstruction addIntArg(int value) {
        arguments.add(value);
        return this;
    }

    public ClientInstruction addStringArg(String value) {
        arguments.add(value);
        return this;
    }

    public ClientInstruction addBoolArg(boolean bool) {
        arguments.add(bool ? 1 : 0);
        return this;
    }

    public void send(Player player) {
        player.getPacketSender().sendClientInstruction(instructionId, arguments.toArray(new Object[0]));
        arguments.clear();
        arguments = null;
    }

    public void clearInstructions() {
        arguments.clear();
        arguments = null;
    }
}
