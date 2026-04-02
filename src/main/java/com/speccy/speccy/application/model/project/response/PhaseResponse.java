package com.speccy.speccy.application.model.project.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhaseResponse {
    private Long id;
    private Long projectId;
    private Long createdBy;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer orderIndex;
    private String status;
    private String accumulatedRules;
}
