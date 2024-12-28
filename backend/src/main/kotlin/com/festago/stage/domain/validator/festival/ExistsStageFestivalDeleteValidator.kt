package com.festago.stage.domain.validator.festival

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.domain.validator.FestivalDeleteValidator
import com.festago.stage.domain.StageRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ExistsStageFestivalDeleteValidator(
    val stageRepository: StageRepository,
) : FestivalDeleteValidator {

    override fun validate(festivalId: Long) {
        if (stageRepository.existsByFestivalId(festivalId)) {
            throw BadRequestException(ErrorCode.FESTIVAL_DELETE_CONSTRAINT_EXISTS_STAGE)
        }
    }
}
