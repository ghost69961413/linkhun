package com.linkhub.controller;

import com.linkhub.dto.skill.SkillRequest;
import com.linkhub.dto.skill.SkillResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(
            @Valid @RequestBody SkillRequest request) {

        SkillResponse response = skillService.createSkill(request);

        return ResponseEntity.ok(
                ApiResponse.<SkillResponse>builder()
                        .success(true)
                        .message("Skill created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills() {

        List<SkillResponse> response = skillService.getAllSkills();

        return ResponseEntity.ok(
                ApiResponse.<List<SkillResponse>>builder()
                        .success(true)
                        .message("Skills fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> addSkill(
            @PathVariable Long skillId) {

        skillService.addSkillToProfile(skillId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Skill added successfully")
                        .build()
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> removeSkill(
            @PathVariable Long skillId) {

        skillService.removeSkillFromProfile(skillId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Skill removed successfully")
                        .build()
        );
    }
}