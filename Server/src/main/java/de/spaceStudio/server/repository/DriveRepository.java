package de.spaceStudio.server.repository;

import de.spaceStudio.server.model.Drive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Integer> {
}
