package com.hrm.project.user_service.controller;

import com.hrm.project.user_service.dto.AuthorityDto;
import com.hrm.project.user_service.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Read and delete endpoints for system-seeded authorities. */
@RestController
@RequestMapping("/api/v1/authorities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthorityController {

    private final AuthorityService authorityService;

    @GetMapping
    public ResponseEntity<List<AuthorityDto>> getAuthorities() {
        return ResponseEntity.ok(authorityService.getAuthorities());
    }

    @DeleteMapping("/{authorityId}")
    public ResponseEntity<String> deleteAuthority(@PathVariable UUID authorityId) {
        authorityService.deleteAuthority(authorityId);
        return new ResponseEntity<>("This authority is deleted", HttpStatus.OK );
    }
}
