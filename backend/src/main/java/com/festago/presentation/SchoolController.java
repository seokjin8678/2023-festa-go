package com.festago.presentation;

import com.festago.application.SchoolService;
import com.festago.dto.SchoolsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schools")
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping
    public ResponseEntity<SchoolsResponse> findAll() {
        return ResponseEntity.ok(schoolService.findAll());
    }
}