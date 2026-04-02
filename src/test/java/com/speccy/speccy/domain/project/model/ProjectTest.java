package com.speccy.speccy.domain.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectTest {

    @Test
    void archiveShouldSetStatusArchived() {
        Project project = new Project(100L, "Core API", "Initial scope");

        project.archive();

        assertEquals(ProjectStatus.ARCHIVED, project.getStatus());
    }

    @Test
    void isOwnerShouldMatchOwnerId() {
        Project project = new Project(200L, "Portal", null);

        assertTrue(project.isOwner(200L));
        assertFalse(project.isOwner(201L));
    }
}
