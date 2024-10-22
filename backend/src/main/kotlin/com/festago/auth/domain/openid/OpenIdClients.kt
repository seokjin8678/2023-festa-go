package com.festago.auth.domain.openid

import com.festago.auth.domain.SocialType
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnexpectedException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.EnumMap

private val log = KotlinLogging.logger {}

class OpenIdClients private constructor(private val openIdClientMap: Map<SocialType, OpenIdClient>) {
    fun getClient(socialType: SocialType): OpenIdClient {
        return openIdClientMap[socialType]
            ?: throw BadRequestException(ErrorCode.OPEN_ID_NOT_SUPPORTED_SOCIAL_TYPE)
    }

    class OpenIdClientsBuilder internal constructor() {
        private val openIdClientMap: MutableMap<SocialType, OpenIdClient> = EnumMap(
            SocialType::class.java
        )

        fun addAll(openIdClients: List<OpenIdClient>): OpenIdClientsBuilder {
            for (openIdClient in openIdClients) {
                add(openIdClient)
            }
            return this
        }

        fun add(openIdClient: OpenIdClient): OpenIdClientsBuilder {
            val socialType = openIdClient.socialType
            if (openIdClientMap.containsKey(socialType)) {
                log.error { "OpenID 제공자는 중복될 수 없습니다." }
                throw UnexpectedException("중복된 OpenID 제공자 입니다.")
            }
            openIdClientMap[socialType] = openIdClient
            return this
        }

        fun build(): OpenIdClients {
            return OpenIdClients(openIdClientMap)
        }
    }

    companion object {
        fun builder(): OpenIdClientsBuilder {
            return OpenIdClientsBuilder()
        }
    }
}
