package com.festago.festival.application;

import com.festago.common.exception.BadRequestException;
import com.festago.common.exception.ErrorCode;
import com.festago.festival.repository.FestivalRepository;
import com.festago.school.application.SchoolDeleteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FestivalSchoolDeleteValidator implements SchoolDeleteValidator {

    private final FestivalRepository festivalRepository;

    @Override
    public void validate(Long schoolId) {
        if (festivalRepository.existsBySchoolId(schoolId)) {
            throw new BadRequestException(ErrorCode.SCHOOL_DELETE_CONSTRAINT_EXISTS_FESTIVAL);
        }
    }
}
