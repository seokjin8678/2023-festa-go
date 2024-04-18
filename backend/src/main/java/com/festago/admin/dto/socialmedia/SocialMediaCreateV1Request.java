package com.festago.admin.dto.socialmedia;

import com.festago.socialmedia.domain.OwnerType;
import com.festago.socialmedia.domain.SocialMediaType;
import com.festago.socialmedia.dto.command.SocialMediaCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SocialMediaCreateV1Request(
    @NotNull
    Long ownerId,
    @NotNull
    OwnerType ownerType,
    @NotNull
    SocialMediaType socialMediaType,
    @NotBlank
    String name,
    @NotBlank
    String logoUrl,
    @NotBlank
    String url
) {

    public SocialMediaCreateCommand toCommand() {
        return new SocialMediaCreateCommand(
            ownerId,
            ownerType,
            socialMediaType,
            name,
            logoUrl,
            url
        );
    }
}