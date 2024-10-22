package com.festago.bookmark.application.command

import com.festago.artist.repository.MemoryArtistRepository
import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.repository.MemoryBookmarkRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.support.fixture.ArtistFixture
import com.festago.support.fixture.BookmarkFixture
import com.festago.support.spec.UnitDescribeSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class ArtistBookmarkCommandServiceTest : UnitDescribeSpec({

    val artistRepository = MemoryArtistRepository()
    val bookmarkRepository = MemoryBookmarkRepository()
    val artistBookmarkCommandService = ArtistBookmarkCommandService(
        bookmarkRepository = bookmarkRepository,
        artistRepository = artistRepository
    )

    val 회원_식별자 = 1234L
    val 브라운 = artistRepository.save(ArtistFixture.builder().name("브라운").build())

    describe("아티스트 북마크를 저장할 때") {

        context("존재하지 않는 아티스트로 저장하면") {

            it("예외가 발생한다.") {
                val ex = shouldThrow<NotFoundException> {
                    artistBookmarkCommandService.save(4885L, 회원_식별자)
                }
                ex shouldHaveMessage ErrorCode.ARTIST_NOT_FOUND.message
            }
        }

        context("최대 북마크 개수를 초과하면") {

            (1..12).forEach { _ ->
                val artist = artistRepository.save(ArtistFixture.builder().build())
                bookmarkRepository.save(
                    BookmarkFixture.builder()
                        .bookmarkType(BookmarkType.ARTIST)
                        .resourceId(artist.id)
                        .memberId(회원_식별자)
                        .build()
                )
            }

            it("예외가 발생한다.") {
                val ex = shouldThrow<BadRequestException> {
                    artistBookmarkCommandService.save(브라운.identifier, 회원_식별자)
                }
                ex shouldHaveMessage ErrorCode.BOOKMARK_LIMIT_EXCEEDED.message
            }
        }

        context("기존 북마크가 존재하면") {
            bookmarkRepository.save(
                BookmarkFixture.builder()
                    .bookmarkType(BookmarkType.ARTIST)
                    .resourceId(브라운.identifier)
                    .memberId(회원_식별자)
                    .build()
            )

            artistBookmarkCommandService.save(브라운.identifier, 회원_식별자)

            it("중복으로 저장되지 않는다.") {
                bookmarkRepository.count() shouldBe 1
            }
        }

        context("기존 북마크가 없으면") {
            artistBookmarkCommandService.save(브라운.identifier, 회원_식별자)

            it("북마크가 저장된다.") {
                bookmarkRepository.count() shouldBe 1
            }
        }
    }

    describe("아티스트 북마크를 삭제할 때") {

        context("저장된 북마크가 없어도") {

            it("예외가 발생하지 않는다.") {
                shouldNotThrowAny {
                    artistBookmarkCommandService.delete(브라운.identifier, 회원_식별자)
                }
            }
        }

        context("저장된 북마크가 있으면") {
            bookmarkRepository.save(
                BookmarkFixture.builder()
                    .bookmarkType(BookmarkType.ARTIST)
                    .resourceId(브라운.identifier)
                    .memberId(회원_식별자)
                    .build()
            )

            artistBookmarkCommandService.delete(브라운.identifier, 회원_식별자)

            it("북마크가 삭제된다.") {
                bookmarkRepository.count() shouldBe 0
            }
        }
    }
})
