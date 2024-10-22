package com.festago.admin.application

import com.festago.admin.repository.AdminFestivalIdResolverQueryDslRepository
import com.festago.admin.repository.AdminStageResolverQueryDslRepository
import com.festago.festival.application.FestivalQueryInfoArtistRenewService
import com.festago.stage.application.StageQueryInfoService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AdminQueryInfoRenewalService(
    private val festivalQueryInfoArtistRenewService: FestivalQueryInfoArtistRenewService,
    private val stageQueryInfoService: StageQueryInfoService,
    private val adminStageResolverQueryDslRepository: AdminStageResolverQueryDslRepository,
    private val adminFestivalIdResolverQueryDslRepository: AdminFestivalIdResolverQueryDslRepository,
) {

    fun renewalByFestivalId(festivalId: Long) {
        festivalQueryInfoArtistRenewService.renewArtistInfo(festivalId)
        for (stage in adminStageResolverQueryDslRepository.findStageByFestivalId(festivalId)) {
            stageQueryInfoService.renewalStageQueryInfo(stage)
        }
    }

    fun renewalByFestivalStartDatePeriod(to: LocalDate, end: LocalDate) {
        val festivalIds = adminFestivalIdResolverQueryDslRepository.findFestivalIdsByStartDatePeriod(to, end)
        log.info { "${festivalIds.size}개의 축제에 대해 QueryInfo를 새로 갱신합니다." }
        for (festivalId in festivalIds) {
            festivalQueryInfoArtistRenewService.renewArtistInfo(festivalId)
        }
        val stages = adminStageResolverQueryDslRepository.findStageByFestivalIdIn(festivalIds)
        for (stage in stages) {
            stageQueryInfoService.renewalStageQueryInfo(stage)
        }
    }
}
