package de.spaceStudio.server.controller;

import de.spaceStudio.server.model.Actor;
import de.spaceStudio.server.model.FightState;
import de.spaceStudio.server.model.Section;
import de.spaceStudio.server.model.Ship;
import de.spaceStudio.server.repository.ActorRepository;
import de.spaceStudio.server.repository.ActorStateRepository;
import de.spaceStudio.server.repository.SectionRepository;
import de.spaceStudio.server.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ActorControllerImpl implements ActorController {


    @Autowired
    ActorRepository actorRepository;

    @Autowired
    ActorStateRepository actorStateRepository;

    @Autowired
    SectionController sectionController;

    @Autowired
    ShipRepository shipRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    GameController gameController;

    @Override
    public Actor getActor(Integer id) {
        Optional<Actor> actor = actorRepository.findById(id);
        if (actor.isPresent()) {
            return actor.get();
        } else throw new IllegalStateException(String.format("Actor %s does not exist", id));
    }

    @Override
    public Actor updateActor(Actor actor) {
        Optional<Actor> fetchActor = actorRepository.findById(actor.getId());
        if (fetchActor.isPresent()) {
            if (!actor.getState().equals(fetchActor.get().getState()))
                gameController.setFightState(actor);

            if (fetchActor.get().getState().getFightState().equals(FightState.WAITING_FOR_TURN)) {
                Optional<Ship> ship = shipRepository.findByOwner(actor);
                if (ship.isPresent()) {
                    Optional<List<Section>> secs = sectionRepository.findAllByShip(ship.get());
                    secs.ifPresent(sections -> sectionController.makeChanges(sections));
                }
            }

            return actorRepository.findById(fetchActor.get().getId()).get();
        } else throw new IllegalStateException(String.format("The Actor %s to update does not exist", actor.getName()));
    }
}