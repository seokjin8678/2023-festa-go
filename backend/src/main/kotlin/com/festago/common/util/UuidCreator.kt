package com.festago.common.util

import com.github.f4b6a3.ulid.UlidCreator
import java.util.UUID

object UuidCreator {

    fun create(): UUID {
        return UlidCreator.getMonotonicUlid().toUuid()
    }
}
