package com.festago.stage.domain.validator.festival

import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.festival.domain.Festival
import com.festago.festival.domain.validator.FestivalUpdateValidator
import com.festago.stage.repository.StageRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 축제를 수정할 때, 축제에 포함된 공연이 수정할 축제 기간의 범위를 벗어난 공연이 있는지 검증하는 클래스
 */
@Component
@Transactional(readOnly = true)
class OutOfDateStageFestivalUpdateValidator(
    private val stageRepository: StageRepository,
) : FestivalUpdateValidator {

    override fun validate(festival: Festival) {
        val stages = stageRepository.findAllByFestivalId(festival.identifier)
        val isOutOfDate = stages.any { festival.isNotInDuration(it.startTime) }
        if (isOutOfDate) {
            throw BadRequestException(ErrorCode.FESTIVAL_UPDATE_OUT_OF_DATE_STAGE_START_TIME)
        }
    }
}
