package com.speccy.speccy.infrastructure.data.project;

import com.speccy.speccy.domain.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);

    @Query(
            """
            select p
            from Project p
            where p.ownerId = :userId
               or p.id in (
                    select pm.projectId
                    from ProjectMember pm
                    where pm.userId = :userId
               )
            """)
    List<Project> findAccessibleByUserId(@Param("userId") Long userId);
}
