package com.festago.bookmark.application.command

import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.repository.MemoryBookmarkRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.festival.repository.MemoryFestivalRepository
import com.festago.support.fixture.BookmarkFixture
import com.festago.support.fixture.FestivalFixture
import com.festago.support.spec.UnitDescribeSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class FestivalBookmarkCommandServiceTest : UnitDescribeSpec({

    val bookmarkRepository = MemoryBookmarkRepository()
    val festivalRepository = MemoryFestivalRepository()
    val festivalBookmarkCommandService = FestivalBookmarkCommandService(
        bookmarkRepository = bookmarkRepository,
        festivalRepository = festivalRepository
    )

    val 회원_식별자 = 1234L
    val 축제 = festivalRepository.save(FestivalFixture.builder().build())

    describe("축제 북마크를 저장할 때") {
        context("존재하지 않는 축제로 저장하면") {
            it("예외가 발생한다.") {
                val ex = shouldThrow<NotFoundException> {
                    festivalBookmarkCommandService.save(4885L, 회원_식별자)
                }
                ex shouldHaveMessage ErrorCode.FESTIVAL_NOT_FOUND.message
            }
        }

        context("최대 북마크 개수를 초과하면") {
            (1..12).forEach { _ ->
                val festival = festivalRepository.save(FestivalFixture.builder().build())
                bookmarkRepository.save(
                    BookmarkFixture.builder()
                        .bookmarkType(BookmarkType.FESTIVAL)
                        .memberId(회원_식별자)
                        .resourceId(festival.id)
                        .build()
                )
            }

            it("예외가 발생한다.") {
                val ex = shouldThrow<BadRequestException> {
                    festivalBookmarkCommandService.save(축제.identifier, 회원_식별자)
                }
                ex shouldHaveMessage ErrorCode.BOOKMARK_LIMIT_EXCEEDED.message
            }
        }

        context("기존 북마크가 존재하면") {
            bookmarkRepository.save(
                BookmarkFixture.builder()
                    .bookmarkType(BookmarkType.FESTIVAL)
                    .memberId(회원_식별자)
                    .resourceId(축제.id)
                    .build()
            )
            festivalBookmarkCommandService.save(축제.identifier, 회원_식별자)

            it("중복으로 저장되지 않는다.") {
                bookmarkRepository.count() shouldBe 1
            }
        }

        context("기존 북마크가 없으면") {
            festivalBookmarkCommandService.save(축제.identifier, 회원_식별자)

            it("북마크가 저장된다.") {
                bookmarkRepository.count() shouldBe 1
            }
        }
    }

    describe("축제 북마크를 삭제할 때") {
        context("저장된 북마크가 없어도") {
            it("예외가 발생하지 않는다.") {
                shouldNotThrowAny {
                    festivalBookmarkCommandService.delete(축제.identifier, 회원_식별자)
                }
            }
        }

        context("저장된 북마크가 있으면") {
            bookmarkRepository.save(
                BookmarkFixture.builder()
                    .bookmarkType(BookmarkType.FESTIVAL)
                    .memberId(회원_식별자)
                    .resourceId(축제.id)
                    .build()
            )

            festivalBookmarkCommandService.delete(축제.identifier, 회원_식별자)

            it("북마크가 삭제된다.") {
                bookmarkRepository.count() shouldBe 0
            }
        }
    }
})
