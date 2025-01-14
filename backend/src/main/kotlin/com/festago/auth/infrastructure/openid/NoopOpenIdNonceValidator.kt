package com.festago.auth.infrastructure.openid

import com.festago.auth.domain.openid.OpenIdNonceValidator
import java.util.Date
import org.springframework.stereotype.Component

/**
 * 2024-04-29 기준 nonce 값 검증 구현에 시간이 소요되므로, nonce 검증을 사용하지 않음.
 *
 * nonce 검증 기능을 추가하면 해당 클래스 삭제할 것
 */
@Component
internal class NoopOpenIdNonceValidator : OpenIdNonceValidator {
    override fun validate(nonce: String?, expiredAt: Date) {
        // noop
    }
}
