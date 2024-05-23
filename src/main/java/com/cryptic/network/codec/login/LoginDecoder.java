package com.cryptic.network.codec.login;

import com.cryptic.network.NetworkUtils;
import com.cryptic.network.codec.ByteBufUtils;
import com.cryptic.network.security.*;
import com.cryptic.utility.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Attempts to decode a player's login request.
 *
 * @author Professor Oak
 */
public final class LoginDecoder extends ByteToMessageDecoder {

    private static final int INITIAL_POW_DIFFICULTY = 15;

    private static final Int2IntMap ipToDifficulty = new Int2IntOpenHashMap();

    static {
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(() -> {
                synchronized (ipToDifficulty) {
                    for (Int2IntMap.Entry entry : ipToDifficulty.int2IntEntrySet()) {
                        int ip = entry.getIntKey();

                        int oldDifficulty = entry.getIntValue();
                        int newDifficulty = oldDifficulty - 1;
                        if (newDifficulty <= INITIAL_POW_DIFFICULTY) {
                            ipToDifficulty.remove(ip);
                        } else {
                            ipToDifficulty.put(ip, newDifficulty);
                        }
                    }
                }
            }, 1, 1, TimeUnit.MINUTES);
    }

    private static final Logger logger = LogManager.getLogger(LoginDecoder.class);

    private int proofOfWorkBlockSize;

    /**
     * The size of the encrypted data.
     */
    private int encryptedLoginBlockSize;

    /**
     * The current login decoder state
     */
    private LoginDecoderState state = LoginDecoderState.LOGIN_REQUEST;

    /**
     * Sends a response code to the client to notify the user logging in.
     *
     * @param ctx The context of the channel handler.
     * @param response The response code to send.
     */
    public static void sendLoginResponse(ChannelHandlerContext ctx, int response) {
        ByteBuf buffer = ctx.alloc().buffer(Byte.BYTES);
        buffer.writeByte(response);
        ctx.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        switch (state) {
            case LOGIN_REQUEST -> decodeRequest(ctx, buffer);
            case PROOF_OF_WORK -> decodeProofOfWorkResponse(ctx, buffer);
            case LOGIN_TYPE_AND_SIZE -> decodeTypeAndSize(ctx, buffer);
            case LOGIN -> decodeLogin(ctx, buffer, out);
        }
    }

    private ProofOfWork proofOfWork;

    private void decodeRequest(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return;
        }

        int request = buffer.readUnsignedByte();
        if (request != NetworkUtils.LOGIN_REQUEST_OPCODE) {
            sendLoginResponse(ctx, LoginResponses.LOGIN_BAD_SESSION_ID);
            return;
        }

        long serverSeed = ThreadLocalSecureRandom.get().nextLong();

        int ip = IPv4AddressExtensionsKt.ipv4Address(ctx).hashCode();
        int difficulty;
        synchronized (ipToDifficulty) {
            difficulty = ipToDifficulty.get(ip);
        }
        if (difficulty == 0) {
            difficulty = INITIAL_POW_DIFFICULTY;
        }
        synchronized (ipToDifficulty) {
            ipToDifficulty.put(ip, difficulty + 1);
        }
        difficulty = 1;

        proofOfWork = ProofOfWork.generate(difficulty);
        byte[] stringBuffer = proofOfWork.getText().getBytes();
        int stringBufferSize = stringBuffer.length;

        int bufSize = Byte.BYTES + Long.BYTES + (/*1 + */2 + 1 + 1 + 1 + stringBufferSize + 1);

        ByteBuf buf = ctx.alloc().buffer(bufSize, bufSize)
            .writeByte(0)
            .writeLong(serverSeed)
            .writeShort(4 + stringBufferSize) // 4 + 45 = 49
            .writeByte(0)
            .writeByte(1)
            .writeByte(difficulty)
            .writeBytes(stringBuffer)
            .writeByte(0);

        ctx.writeAndFlush(buf, ctx.voidPromise());

        state = LoginDecoderState.PROOF_OF_WORK;
    }

    private void decodeProofOfWorkResponse(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (!buffer.isReadable(2 + 8)) {
            return;
        }

        int size = buffer.readUnsignedShort();
        long nonce = buffer.readLong();
        if (proofOfWork.validate(nonce)) {
            state = LoginDecoderState.LOGIN_TYPE_AND_SIZE;
        } else {
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
        }
    }

    private void decodeTypeAndSize(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (!buffer.isReadable(2)) {
            return;
        }

        int connectionType = buffer.readUnsignedByte();
        if (connectionType != NetworkUtils.NEW_CONNECTION_OPCODE
            && connectionType != NetworkUtils.RECONNECTION_OPCODE) {
            //logger.error("Session rejected for bad connection type id: {}", box(connectionType));
            sendLoginResponse(ctx, LoginResponses.LOGIN_BAD_SESSION_ID);
            return;
        }

        encryptedLoginBlockSize = buffer.readUnsignedByte();

        state = LoginDecoderState.LOGIN;
    }

    private void decodeLogin(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (!buffer.isReadable(encryptedLoginBlockSize)) {
            return;
        }

        // obviously adjust the indentation below:
        int magicId = buffer.readUnsignedByte();
        if (magicId != 0xFF) {
            //logger.error(String.format("[host= %s] [magic= %d] was rejected for the wrong magic value.", ctx.channel().remoteAddress(), magicId));
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

        int memory = buffer.readByte();
        if (memory != 0 && memory != 1) {
            //logger.error("[host={}] was rejected for having the memory setting.", ctx.channel().remoteAddress());
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

        buffer.markReaderIndex();
        int length = buffer.readUnsignedByte();
        if (buffer.readableBytes() < length) {
            buffer.resetReaderIndex();
            return;
        }

        ByteBuf rsaBuffer = Rsa.rsa(buffer.readSlice(length));

        int securityId = rsaBuffer.readByte();

        if (securityId != 10) {
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

        int[] clientseed = {rsaBuffer.readInt(), rsaBuffer.readInt(), rsaBuffer.readInt(), rsaBuffer.readInt()};
        int[] serverKeys = new int[4];
        rsaBuffer.readLong(); //read server seed

        IsaacRandom cipher = new IsaacRandom(clientseed);
        for (int i = 0; i < serverKeys.length; i++) {
            serverKeys[i] += 50 + clientseed[i];
        }

        IsaacRandom encryption = new IsaacRandom(serverKeys);
        String uid = ByteBufUtils.readString(rsaBuffer);
        String username = Utils.formatText(ByteBufUtils.readString(rsaBuffer));
        String password = ByteBufUtils.readString(rsaBuffer);
        String mac = ByteBufUtils.readString(rsaBuffer);

        if (username.isEmpty() || username.length() > 12 || password.length() < 3 || password.length() > 20) {
            sendLoginResponse(ctx, LoginResponses.INVALID_CREDENTIALS_COMBINATION);
            return;
        }

        String hostName = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostName();

        if(HostBlacklist.isBlocked(hostName)) {
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

        out.add(new LoginDetailsMessage(ctx, username, password, ByteBufUtils.getHost(ctx.channel()), mac, uid, encryption, cipher));
    }

    private enum LoginDecoderState {
        LOGIN_REQUEST, PROOF_OF_WORK, LOGIN_TYPE_AND_SIZE, LOGIN
    }
}
