package com.speccy.speccy.domain.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhaseTest {

    @Test
    void isEditableShouldBeTrueForPlanningAndActiveOnly() {
        Phase phase =
                new Phase(
                        1L,
                        2L,
                        "Sprint 1",
                        "Phase description",
                        LocalDate.parse("2026-04-01"),
                        LocalDate.parse("2026-04-30"),
                        1,
                        null);

        assertTrue(phase.isEditable());

        phase.activate();
        assertTrue(phase.isEditable());

        phase.complete();
        assertFalse(phase.isEditable());
    }

    @Test
    void updateInfoShouldChangeCoreFields() {
        Phase phase =
                new Phase(
                        1L,
                        2L,
                        "Sprint 1",
                        "Old description",
                        LocalDate.parse("2026-04-01"),
                        LocalDate.parse("2026-04-30"),
                        1,
                        "old-rules");

        phase.updateInfo(
                "Sprint 1 - Updated",
                "New description",
                LocalDate.parse("2026-05-01"),
                LocalDate.parse("2026-05-31"));
        phase.updateAccumulatedRules("new-rules");

        assertEquals("Sprint 1 - Updated", phase.getName());
        assertEquals("New description", phase.getDescription());
        assertEquals(LocalDate.parse("2026-05-01"), phase.getStartDate());
        assertEquals(LocalDate.parse("2026-05-31"), phase.getEndDate());
        assertEquals("new-rules", phase.getAccumulatedRules());
    }
}
