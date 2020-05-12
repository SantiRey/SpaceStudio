package org.spacestudio.service;

import com.google.inject.Inject;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class SpaceShipService {
    @Inject
    EntityManager em;

    @Transactional
    public void createShip(String ShipDescription) {
        org.spacestudio.openapi.space.model.Ship ship = new org.spacestudio.openapi.space.model.Ship();
        em.persist(ship);
    }
}
