package de.spaceStudio.server.repository;

import de.spaceStudio.server.model.Planet;
import de.spaceStudio.server.model.Ship;
import de.spaceStudio.server.model.StopAbstract;
import de.spaceStudio.server.model.Universe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StopAbstractRepository extends JpaRepository<StopAbstract, Integer> {
    Optional<StopAbstract> findByShips(Ship ship);

    List<StopAbstract> findByUniverse(Universe universe);

    Optional<StopAbstract> findByName(String name);

    void deleteAllByUniverse(Universe universe);
}
