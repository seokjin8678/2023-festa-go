package com.festago.bookmark.application.command

import com.festago.bookmark.domain.BookmarkType
import com.festago.bookmark.repository.MemoryBookmarkRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.repository.MemorySchoolRepository
import com.festago.support.fixture.BookmarkFixture
import com.festago.support.fixture.SchoolFixture
import com.festago.support.spec.UnitDescribeSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class SchoolBookmarkCommandServiceTest : UnitDescribeSpec({

    val bookmarkRepository = MemoryBookmarkRepository()
    val schoolRepository = MemorySchoolRepository()
    val schoolBookmarkCommandService = SchoolBookmarkCommandService(
        bookmarkRepository = bookmarkRepository,
        schoolRepository = schoolRepository
    )

    val 회원_식별자 = 4885L
    val 학교 = schoolRepository.save(SchoolFixture.builder().build())

    describe("학교 북마크를 저장할 때") {
        context("존재하지 않는 학교로 저장하면") {
            it("예외가 발생한다.") {
                val ex = shouldThrow<NotFoundException> {
                    schoolBookmarkCommandService.save(4885L, 회원_식별자)
                }
                ex shouldHaveMessage ErrorCode.SCHOOL_NOT_FOUND.message
            }
        }

        context("최대 북마크 개수를 초과하면") {
            (1..12).forEach { _ ->
                val school = schoolRepository.save(SchoolFixture.builder().build())
                bookmarkRepository.save(
                    BookmarkFixture.builder()
                        .bookmarkType(BookmarkType.SCHOOL)
                        .resourceId(school.id)
                        .memberId(회원_식별자)
                        .build()
                )
            }

            it("예외가 발생한다.") {
                val ex = shouldThrow<BadRequestException> {
                    schoolBookmarkCommandService.save(학교.id, 회원_식별자)
                }
                ex shouldHaveMessage ErrorCode.BOOKMARK_LIMIT_EXCEEDED.message
            }
        }

        context("기존 북마크가 존재하면") {
            bookmarkRepository.save(
                BookmarkFixture.builder()
                    .bookmarkType(BookmarkType.SCHOOL)
                    .resourceId(학교.id)
                    .memberId(회원_식별자)
                    .build()
            )

            schoolBookmarkCommandService.save(학교.id!!, 회원_식별자)

            it("중복으로 저정되지 않는다") {
                schoolRepository.count() shouldBe 1
            }
        }

        context("기존 북마크가 없으면") {
            schoolBookmarkCommandService.save(학교.id!!, 회원_식별자)

            it("북마크가 저장된다.") {
                schoolRepository.count() shouldBe 1
            }
        }
    }

    describe("학교 북마크를 삭제할 때") {
        context("저장된 북마크가 없어도") {
            it("예외가 발생하지 않는다.") {
                shouldNotThrowAny {
                    schoolBookmarkCommandService.delete(학교.id!!, 회원_식별자)
                }
            }
        }

        context("저장된 북마크가 있으면") {
            bookmarkRepository.save(
                BookmarkFixture.builder()
                    .bookmarkType(BookmarkType.SCHOOL)
                    .resourceId(학교.id)
                    .memberId(회원_식별자)
                    .build()
            )
            schoolBookmarkCommandService.delete(학교.id!!, 회원_식별자)

            it("북마크가 삭제된다.") {
                bookmarkRepository.count() shouldBe 0
            }
        }
    }
})