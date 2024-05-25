package com.cryptic.network.security

import java.security.SecureRandom

/**
 * @author Jire
 */
object ThreadLocalSecureRandom {

    private val threadLocal = ThreadLocal.withInitial { SecureRandom() }

    @JvmStatic
    @JvmName("get")
    operator fun invoke(): SecureRandom = threadLocal.get()

}
