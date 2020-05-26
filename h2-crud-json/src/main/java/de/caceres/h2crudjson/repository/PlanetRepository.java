package de.caceres.h2crudjson.repository;

import de.caceres.h2crudjson.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetRepository extends JpaRepository<Planet, Integer> {
}