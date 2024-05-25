package com.cryptic.network.security

import com.google.common.io.BaseEncoding
import org.apache.commons.codec.digest.DigestUtils
import java.math.BigInteger


/**
 * @author Jire
 */
data class ProofOfWork(
    val difficulty: Int,
    val text: String
) {

    fun validate(nonce: Long): Boolean {
        val stringToHash = 1.toHexString() + difficulty.toHexString() + text + nonce.toInt().toHexString()
        val hashedString = DigestUtils.sha256(stringToHash)
        return hashedString.getLeadingZeroBits() >= difficulty
    }

    private fun Int.toHexString(): String = Integer.toHexString(this)

    private fun ByteArray.getLeadingZeroBits(): Int {
        var bits = 0
        forEach { value ->
            val numberOfTrailingBits = value.getLeadingZeroBits().also { bits += it }
            if (numberOfTrailingBits != 8) return bits
        }
        return bits
    }

    private fun Byte.getLeadingZeroBits(): Int = when (val value = this.toInt()) {
        0 -> 8
        else -> {
            var bits = 0
            var shiftedValue = value and 0xFF
            while ((shiftedValue and 0x80) == 0) {
                bits++
                shiftedValue = shiftedValue shl 1
            }
            bits
        }
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun generate(difficulty: Int, world: Int = 1): ProofOfWork {
            /* this real Jagex generation from Polar lol */
            val timestamp = System.currentTimeMillis()
            val randomData = ByteArray(495)
            ThreadLocalSecureRandom().nextBytes(randomData)

            val string =
                java.lang.Long.toHexString(timestamp) +
                        Integer.toHexString(world) +
                        BigInteger(randomData).toString(16)
            val array = string.toByteArray()

            val text = BaseEncoding.base64Url().encode(array)

            return ProofOfWork(difficulty, text)
        }

    }

}
