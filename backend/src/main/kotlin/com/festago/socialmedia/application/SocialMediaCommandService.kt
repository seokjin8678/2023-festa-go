package com.festago.socialmedia.application

import com.festago.artist.domain.ArtistRepository
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.NotFoundException
import com.festago.school.domain.SchoolRepository
import com.festago.socialmedia.domain.OwnerType
import com.festago.socialmedia.domain.SocialMedia
import com.festago.socialmedia.domain.SocialMediaRepository
import com.festago.socialmedia.domain.getOrThrow
import com.festago.socialmedia.dto.command.SocialMediaCreateCommand
import com.festago.socialmedia.dto.command.SocialMediaUpdateCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SocialMediaCommandService(
    private val socialMediaRepository: SocialMediaRepository,
    private val schoolRepository: SchoolRepository,
    private val artistRepository: ArtistRepository,
) {

    fun createSocialMedia(command: SocialMediaCreateCommand): Long {
        validateCreate(command)
        val (ownerId, ownerType, socialMediaType, name, logoUrl, url) = command
        val socialMedia = socialMediaRepository.save(
            SocialMedia(
                ownerId = ownerId,
                ownerType = ownerType,
                mediaType = socialMediaType,
                name = name,
                logoUrl = logoUrl,
                url = url
            )
        )
        return socialMedia.identifier
    }

    private fun validateCreate(command: SocialMediaCreateCommand) {
        val ownerId = command.ownerId
        val ownerType = command.ownerType
        val socialMediaType = command.socialMediaType
        if (socialMediaRepository.existsByOwnerIdAndOwnerTypeAndMediaType(ownerId, ownerType, socialMediaType)) {
            throw BadRequestException(ErrorCode.DUPLICATE_SOCIAL_MEDIA)
        }
        if (ownerType == OwnerType.ARTIST && !artistRepository.existsById(ownerId)) {
            throw NotFoundException(ErrorCode.ARTIST_NOT_FOUND)
        }
        if (ownerType == OwnerType.SCHOOL && !schoolRepository.existsById(ownerId)) {
            throw NotFoundException(ErrorCode.SCHOOL_NOT_FOUND)
        }
    }

    fun updateSocialMedia(socialMediaId: Long, command: SocialMediaUpdateCommand) {
        val socialMedia = socialMediaRepository.getOrThrow(socialMediaId)
        val (name, url, logoUrl) = command
        socialMedia.changeName(name)
        socialMedia.changeUrl(url)
        socialMedia.changeLogoUrl(logoUrl)
    }

    fun deleteSocialMedia(socialMediaId: Long) {
        socialMediaRepository.deleteById(socialMediaId)
    }
}
