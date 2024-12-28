package com.festago.artist.application.integration

import com.festago.artist.application.ArtistSearchV1QueryService
import com.festago.artist.domain.ArtistAlias
import com.festago.artist.domain.ArtistAliasRepository
import com.festago.artist.domain.ArtistRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.support.ApplicationIntegrationTest
import com.festago.support.fixture.ArtistFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class ArtistSearchV1QueryServiceIntegrationTest : ApplicationIntegrationTest() {

    @Autowired
    lateinit var artistSearchV1QueryService: ArtistSearchV1QueryService

    @Autowired
    lateinit var artistRepository: ArtistRepository

    @Autowired
    lateinit var artistAliasRepository: ArtistAliasRepository

    @Test
    fun 검색어가_한글자면_동등검색을_한다() {
        // given
        artistRepository.save(ArtistFixture.builder().name("난못").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("못난").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("못").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }

        // when
        val actual = artistSearchV1QueryService.findAllByKeyword("못")

        // then
        actual shouldHaveSize 1
        actual.first().name shouldBe "못"
    }

    @Test
    fun 검색어가_두글자_이상이면_like검색을_한다() {
        // given
        artistRepository.save(ArtistFixture.builder().name("에이핑크").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("블랙핑크").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("핑크").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("핑크 플로이드").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("핑").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        artistRepository.save(ArtistFixture.builder().name("크").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }

        // when
        val actual = artistSearchV1QueryService.findAllByKeyword("핑크")

        // then
        actual shouldHaveSize 4
    }

    @Test
    fun 아티스트명은_영어_한국어_순으로_오름차순_정렬된다() {
        // given
        val 가_아티스트 = artistRepository.save(ArtistFixture.builder().name("가_아티스트").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        val A_아티스트 = artistRepository.save(ArtistFixture.builder().name("A_아티스트").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        val 나_아티스트 = artistRepository.save(ArtistFixture.builder().name("나_아티스트").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        val C_아티스트 = artistRepository.save(ArtistFixture.builder().name("C_아티스트").build())
            .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }

        // when
        val actual = artistSearchV1QueryService.findAllByKeyword("아티스트")

        // then
        actual.map { it.id } shouldContainExactly listOf(
            A_아티스트.identifier,
            C_아티스트.identifier,
            가_아티스트.identifier,
            나_아티스트.identifier
        )
    }

    @Test
    fun 검색결과가_10개_이상이면_예외() {
        // given
        for (i in 0..9) {
            artistRepository.save(ArtistFixture.builder().name("핑크").build())
                .also { artistAliasRepository.save(ArtistAlias(artistId = it.identifier, alias = it.name)) }
        }

        // when
        val ex = shouldThrow<BadRequestException> {
            artistSearchV1QueryService.findAllByKeyword("핑크")
        }

        // then
        ex shouldHaveMessage ErrorCode.BROAD_SEARCH_KEYWORD.message
    }

    @Test
    fun 검색_결과가_없다면_빈리스트_반환() {
        // when
        val actual = artistSearchV1QueryService.findAllByKeyword("없음")

        // then
        actual shouldHaveSize 0
    }
}
