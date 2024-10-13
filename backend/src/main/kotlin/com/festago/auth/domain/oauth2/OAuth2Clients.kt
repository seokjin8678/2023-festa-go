package com.festago.auth.domain.oauth2

import com.festago.auth.domain.SocialType
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.UnexpectedException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.EnumMap

private val log = KotlinLogging.logger {}

class OAuth2Clients private constructor(
    private val oAuth2ClientMap: Map<SocialType, OAuth2Client>,
) {

    fun getClient(socialType: SocialType): OAuth2Client {
        return oAuth2ClientMap[socialType]
            ?: throw BadRequestException(ErrorCode.OAUTH2_NOT_SUPPORTED_SOCIAL_TYPE)
    }

    class OAuth2ClientsBuilder internal constructor() {
        private val oAuth2ClientMap: MutableMap<SocialType, OAuth2Client> = EnumMap(
            SocialType::class.java
        )

        fun addAll(oAuth2Clients: List<OAuth2Client>): OAuth2ClientsBuilder {
            for (oAuth2Client in oAuth2Clients) {
                add(oAuth2Client)
            }
            return this
        }

        fun add(oAuth2Client: OAuth2Client): OAuth2ClientsBuilder {
            val socialType = oAuth2Client.socialType
            if (oAuth2ClientMap.containsKey(socialType)) {
                log.error { "OAuth2 제공자는 중복될 수 없습니다." }
                throw UnexpectedException("중복된 OAuth2 제공자 입니다.")
            }
            oAuth2ClientMap[socialType] = oAuth2Client
            return this
        }

        fun build(): OAuth2Clients {
            return OAuth2Clients(oAuth2ClientMap)
        }
    }

    companion object {
        fun builder(): OAuth2ClientsBuilder {
            return OAuth2ClientsBuilder()
        }
    }
}
