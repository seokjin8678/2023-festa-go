package com.festago.festival.application

import com.festago.festival.domain.FestivalQueryInfo.Companion.create
import com.festago.festival.dto.event.FestivalCreatedEvent
import com.festago.festival.dto.event.FestivalDeletedEvent
import com.festago.festival.repository.FestivalQueryInfoRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class FestivalQueryInfoEventListener(
    private val festivalQueryInfoRepository: FestivalQueryInfoRepository
) {

    /**
     * 해당 이벤트는 비동기로 실행하면 문제가 발생할 수 있으니, 동기적으로 처리해야함 <br></br> 축제가 생성되면 FestivalQueryInfo는 반드시! 생성되어야 함
     */
    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    fun createFestivalInfo(event: FestivalCreatedEvent) {
        val festivalQueryInfo = create(event.festival.id!!)
        festivalQueryInfoRepository.save(festivalQueryInfo)
    }

    /**
     * 삭제의 경우 동기적으로 처리될 필요가 없음 <br></br> 하지만 일관성을 위해 동기적으로 처리함
     */
    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    fun deleteFestivalInfo(event: FestivalDeletedEvent) {
        festivalQueryInfoRepository.deleteByFestivalId(event.festivalId)
    }
}
