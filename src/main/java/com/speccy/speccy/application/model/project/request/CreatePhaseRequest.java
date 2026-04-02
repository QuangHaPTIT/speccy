package com.speccy.speccy.application.model.project.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.speccy.speccy.application.constants.DateTimeConstants;
import com.speccy.speccy.application.model.project.request.validation.ValidPhaseDateRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@ValidPhaseDateRange
public class CreatePhaseRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @JsonFormat(pattern = DateTimeConstants.DATE_FORMAT)
    private LocalDate startDate;

    @JsonFormat(pattern = DateTimeConstants.DATE_FORMAT)
    private LocalDate endDate;

    private String accumulatedRules;
}
