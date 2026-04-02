package com.speccy.speccy.application.model.project.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String status;
    private String currentUserRole;
}
