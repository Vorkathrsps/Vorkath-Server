package com.cryptic.network.security

import com.cryptic.network.NetworkUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import java.math.BigInteger

/**
 * @author Jire
 */
object Rsa {

    @JvmStatic
    fun ByteBuf.toBigInteger(): BigInteger? {
        val bytes = ByteBufUtil.getBytes(this, readerIndex(), readableBytes(), false)
        if(bytes.isEmpty()) return null
        return BigInteger(bytes)
    }

    @JvmStatic
    fun BigInteger.toByteBuf(): ByteBuf {
        return Unpooled.wrappedBuffer(toByteArray())
    }

    @JvmStatic
    fun ByteBuf.rsa(): ByteBuf? {
        return toBigInteger()?.modPow(NetworkUtils.RSA_EXPONENT, NetworkUtils.RSA_MODULUS)?.toByteBuf()
    }

}
