package de.bremen.server.repository;

import de.bremen.server.model.Beam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeamRepository extends JpaRepository<Beam, Integer> {
}