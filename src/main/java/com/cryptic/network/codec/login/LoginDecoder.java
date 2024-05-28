package com.cryptic.network.codec.login;

import com.cryptic.network.NetworkUtils;
import com.cryptic.network.codec.ByteBufUtils;
import com.cryptic.network.security.*;
import com.cryptic.utility.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Attempts to decode a player's login request.
 *
 * @author Professor Oak
 */
/**
 * Attempts to decode a player's login request.
 *
 * @author Professor Oak
 */
public final class LoginDecoder extends ByteToMessageDecoder {

    private static final int INITIAL_POW_DIFFICULTY = 15;

    private static final Logger logger = LogManager.getLogger(LoginDecoder.class);

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
     * @param ctx      The context of the channel handler.
     * @param response The response code to send.
     */
    public static void sendLoginResponse(final ChannelHandlerContext ctx, final int response) {
        if (ctx == null || ctx.isRemoved()) return;

        final Channel channel = ctx.channel();

        if (channel == null) {
            ctx.close();
            return;
        }

        ByteBuf buffer = ctx.alloc().buffer(Byte.BYTES);
        if (buffer.isWritable()) {
            buffer.writeByte(response);
            ctx.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (ctx == null || ctx.isRemoved()) return;
        if (!buffer.isReadable()) {
            closeChannel(ctx, "Buffer not readable");
            return;
        }

        switch (state) {
            case LOGIN_REQUEST -> decodeRequest(ctx, buffer);
            case PROOF_OF_WORK -> decodeProofOfWorkResponse(ctx, buffer);
            case LOGIN_TYPE_AND_SIZE -> decodeTypeAndSize(ctx, buffer);
            case LOGIN -> decodeLogin(ctx, buffer, out);
        }
    }

    private ProofOfWork proofOfWork;

    private void decodeRequest(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (ctx == null || !buffer.isReadable() || ctx.isRemoved()) {
            closeChannel(ctx, "Context removed or buffer not readable");
            return;
        }

        int loginOpcode = buffer.readUnsignedByte();
        if (loginOpcode != NetworkUtils.LOGIN_REQUEST_OPCODE) {
            closeChannel(ctx, "Invalid login opcode");
            return;
        }

        long serverSeed = ThreadLocalSecureRandom.get().nextLong();

        proofOfWork = ProofOfWork.generate(INITIAL_POW_DIFFICULTY);
        byte[] stringBuffer = proofOfWork.getText().getBytes();
        int stringBufferSize = stringBuffer.length;

        int bufSize = Byte.BYTES + Long.BYTES + (/*1 + */2 + 1 + 1 + 1 + stringBufferSize + 1);

        ByteBuf buf = ctx.alloc().buffer(bufSize, bufSize)
            .writeByte(0)
            .writeLong(serverSeed)
            .writeShort(4 + stringBufferSize) // 4 + 45 = 49
            .writeByte(0)
            .writeByte(1)
            .writeByte(INITIAL_POW_DIFFICULTY)
            .writeBytes(stringBuffer)
            .writeByte(0);

        ctx.writeAndFlush(buf, ctx.voidPromise());
        state = LoginDecoderState.PROOF_OF_WORK;
    }

    private void decodeProofOfWorkResponse(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (ctx == null || ctx.isRemoved()) {
            closeChannel(ctx, "Context removed or buffer not readable for proof of work");
            return;
        }

        if (!buffer.isReadable(3 + 8)) {
            return;
        }

        int op = buffer.readByte();

        if (op != 19) {
            closeChannel(ctx, "Invalid proof of work operation");
            return;
        }

        buffer.readUnsignedShort(); // size is unused
        long nonce = buffer.readLong();

        if (proofOfWork.validate(nonce)) {
            state = LoginDecoderState.LOGIN_TYPE_AND_SIZE;
        } else {
            closeChannel(ctx, "Proof of work validation failed");
        }
    }

    private void decodeTypeAndSize(ChannelHandlerContext ctx, ByteBuf buffer) {
        if (ctx == null || ctx.isRemoved() || !buffer.isReadable(2)) {
            closeChannel(ctx, "Context removed or buffer not readable for type and size");
            return;
        }

        int connectionType = buffer.readUnsignedByte();
        if (connectionType != NetworkUtils.NEW_CONNECTION_OPCODE && connectionType != NetworkUtils.RECONNECTION_OPCODE) {
            closeChannel(ctx, "Failed to decode type and size");
            return;
        }

        encryptedLoginBlockSize = buffer.readUnsignedByte();
        state = LoginDecoderState.LOGIN;
    }

    private void decodeLogin(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
        if (ctx == null || ctx.isRemoved()) return;

        if (encryptedLoginBlockSize != buffer.readableBytes()) {
            closeChannel(ctx, "Encrypted login block size mismatch");
            return;
        }

        if (!buffer.isReadable(encryptedLoginBlockSize)) {
            closeChannel(ctx, "Buffer not readable for encrypted login block size");
            return;
        }

        if (buffer.readerIndex() > buffer.writerIndex()) {
            buffer.skipBytes(buffer.readableBytes());
            closeChannel(ctx, "Buffer reader index greater than writer index");
            return;
        }

        int magicId = buffer.readUnsignedByte();
        if (magicId != 0xFF) {
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

        int memory = buffer.readByte();
        if (memory != 0 && memory != 1) {
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
        if (rsaBuffer == null || !rsaBuffer.isReadable()) {
            if (rsaBuffer != null) rsaBuffer.release();
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

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
        rsaBuffer.release();

        if (username.isEmpty() || username.length() > 12 || password.length() < 3 || password.length() > 20) {
            sendLoginResponse(ctx, LoginResponses.INVALID_CREDENTIALS_COMBINATION);
            return;
        }

        String hostName = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostName();
        if (HostBlacklist.isBlocked(hostName)) {
            sendLoginResponse(ctx, LoginResponses.LOGIN_REJECT_SESSION);
            return;
        }

        out.add(new LoginDetailsMessage(ctx, username, password, ByteBufUtils.getHost(ctx.channel()), mac, uid, encryption, cipher));
    }

    private void closeChannel(ChannelHandlerContext ctx, String reason) {
       // logger.info("Closing channel: {}", reason);
        if (ctx != null && ctx.channel() != null && ctx.channel().isOpen()) {
            ctx.channel().close();
        }
    }

    private enum LoginDecoderState {
        LOGIN_REQUEST, PROOF_OF_WORK, LOGIN_TYPE_AND_SIZE, LOGIN
    }

}
