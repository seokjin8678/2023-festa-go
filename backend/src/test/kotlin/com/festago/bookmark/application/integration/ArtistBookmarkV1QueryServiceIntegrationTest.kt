package com.festago.bookmark.application.integration

import com.festago.artist.repository.ArtistRepository
import com.festago.bookmark.application.ArtistBookmarkV1QueryService
import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.repository.BookmarkRepository
import com.festago.member.repository.MemberRepository
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.BookmarkFixture
import com.festago.support.fixture.MemberFixture
import com.festago.support.spec.IntegrationDescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize

class ArtistBookmarkV1QueryServiceIntegrationTest(
    val artistBookmarkV1QueryService: ArtistBookmarkV1QueryService,
    val artistRepository: ArtistRepository,
    val memberRepository: MemberRepository,
    val bookmarkRepository: BookmarkRepository,
) : IntegrationDescribeSpec({

    val 아티스트A = artistRepository.save(ArtistFixture.builder().name("아티스트A").build())
    val 아티스트B = artistRepository.save(ArtistFixture.builder().name("아티스트B").build())
    val 회원A = memberRepository.save(MemberFixture.builder().build())
    val 회원B = memberRepository.save(MemberFixture.builder().build())
    val 회원C = memberRepository.save(MemberFixture.builder().build())

    fun createBookmark(memberId: Long, artistId: Long) {
        bookmarkRepository.save(
            BookmarkFixture.builder()
                .bookmarkType(BookmarkType.ARTIST)
                .resourceId(artistId)
                .memberId(memberId)
                .build()
        )
    }

    describe("북마크한 아티스트 목록을 조회할 때") {

        context("회원별로 북마크한 아티스트를 등록하면") {
            createBookmark(회원A.identifier, 아티스트A.identifier)
            createBookmark(회원A.identifier, 아티스트B.identifier)
            createBookmark(회원B.identifier, 아티스트A.identifier)

            it("회원A는 아티스트A, B에 대한 목록이 반환된다.") {
                val actual = artistBookmarkV1QueryService.findArtistBookmarksByMemberId(회원A.identifier)

                actual.map { it.artist.id } shouldContainExactlyInAnyOrder listOf(아티스트A.identifier, 아티스트B.identifier)
            }

            it("회원B는 아티스트A에 대한 목록이 반환된다.") {
                val actual = artistBookmarkV1QueryService.findArtistBookmarksByMemberId(회원B.identifier)

                actual.map { it.artist.id } shouldContain 아티스트A.identifier
            }

            it("회원C는 빈 목록이 반환된다.") {
                val actual = artistBookmarkV1QueryService.findArtistBookmarksByMemberId(회원C.identifier)

                actual shouldHaveSize 0
            }
        }
    }
})
