package com.aelous.utility.flood;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.aelous.GameServer;
import com.aelous.network.codec.login.LoginResponses;
import com.aelous.network.security.IsaacRandom;

/**
 * Represents a client which will attempt
 * to connect to the server.
 * 
 * This can be used to stresstest the server.
 * 
 * Note: Code was copy+pasted from client.
 * I've barely touched it.
 * 
 * @author Professor Oak
 */
public class FloodClient {

    public FloodClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private final String username;
    private final String password;
    private Buffer login;
    private Buffer incoming;
    private Buffer outgoing;
    private BufferedConnection socketStream;
    private long serverSeed;
    private IsaacRandom cipher;
    private IsaacRandom encryption;
    public boolean loggedIn;
    private int pingCounter = 0;
    
    public void attemptLogin() throws Exception {
        login = Buffer.create();
        incoming = Buffer.create();
        outgoing = Buffer.create();
        socketStream = new BufferedConnection(openSocket(GameServer.properties().gamePort));

        outgoing.writeByte(14); // REQUEST
        socketStream.queueBytes(1, outgoing.payload);

        int response = socketStream.read();
        System.out.println("bot response is " + response);
        if (response == 0) {
            socketStream.flushInputStream(incoming.payload, 8);
            incoming.currentPosition = 0;
            serverSeed = incoming.readLong(); // aka server session key
            System.out.println("server seed is " + serverSeed);
            int seed[] = new int[4];
            seed[0] = (int) (Math.random() * 99999999D);
            seed[1] = (int) (Math.random() * 99999999D);
            seed[2] = (int) (serverSeed >> 32);
            seed[3] = (int) serverSeed;
            outgoing.currentPosition = 0;
            outgoing.writeByte(10);
            outgoing.writeInt(seed[0]);
            outgoing.writeInt(seed[1]);
            outgoing.writeInt(seed[2]);
            outgoing.writeInt(seed[3]);
            outgoing.writeString(GameServer.properties().gameVersion);
            outgoing.writeString(username);
            outgoing.writeString(password);
            outgoing.writeString("");
            outgoing.writeString("");
            outgoing.encryptRSAContent();

            login.currentPosition = 0;
            login.writeByte(16); // 18 if reconnecting, we aren't though
            login.writeByte(outgoing.currentPosition + 2); // size of the
            // login block
            login.writeByte(255);
            login.writeByte(0); // low mem
            login.writeBytes(outgoing.payload, outgoing.currentPosition, 0);
            cipher = new IsaacRandom(seed);
            for (int index = 0; index < 4; index++)
                seed[index] += 50;

            encryption = new IsaacRandom(seed);
            socketStream.queueBytes(login.currentPosition, login.payload);
            response = socketStream.read();
            System.out.println("bot response is now " + response);
        }

        if (response == LoginResponses.LOGIN_SUCCESSFUL) {
            GameServer.getFlooder().clients.put(username, this);
            int rights = socketStream.read();
            loggedIn = true;
            outgoing.currentPosition = 0;
            incoming.currentPosition = 0;
        }
    }


    public void process() throws Exception {
        if (loggedIn) {
            /*
             * for (int i = 0; i < 5; i++) {
             *  if (!readPacket())
             *      break;
             * }
             */
            if (pingCounter++ >= 25) {
                pingCounter = 0;
                writePacket(new PacketBuilder(0)); // Basic ping
            }

            if (socketStream != null && outgoing.currentPosition > 0) {
                socketStream.queueBytes(outgoing.currentPosition, outgoing.payload);
                outgoing.currentPosition = 0;
            }
        }
    }

    private boolean readPacket() throws Exception {
        if (socketStream == null) {
            return false;
        }

        int available = socketStream.available();
        if (available < 2) {
            return false;
        }

        int opcode = -1;
        int packetSize = -1;

        //First we read opcode...
        if (opcode == -1) {

            socketStream.flushInputStream(incoming.payload, 1);

            opcode = incoming.payload[0] & 0xff;

            if (encryption != null) {
                opcode = opcode - encryption.nextInt() & 0xff;
            }

            //Now attempt to read packet size..
            socketStream.flushInputStream(incoming.payload, 2);
            packetSize = ((incoming.payload[0] & 0xff) << 8)
                    + (incoming.payload[1] & 0xff);

        }

        if (!(opcode >= 0 && opcode < 256)) {
            opcode = -1;
            return false;
        }

        incoming.currentPosition = 0;
        socketStream.flushInputStream(incoming.payload, packetSize);

        switch(opcode) {

        }
        return false;
    }

    private void writePacket(PacketBuilder builder) {
        byte[] buffer = new byte[builder.getPosition() + 2];

        // Put opcode
        buffer[0] = (byte) (builder.getOpcode() + cipher.nextInt());

        // Put size
        buffer[1] = (byte) (builder.getPosition());

        // Copy rest of the packet data
        for (int i = 2; i < buffer.length; i++) {
            buffer[i] = builder.getBuffer()[i - 2];
        }

        outgoing.writeBytes(buffer);
    }

    private Socket openSocket(int port) throws IOException {
        return new Socket(InetAddress.getByName("localhost"), port);
    }
}
