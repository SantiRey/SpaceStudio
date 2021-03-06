package de.spaceStudio.server.model;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;

@Entity
public class Planet extends StopAbstract {


    /**
     * Empty constructor
     */
    public Planet() {

    }

    public Planet(PlanetBuilder builder) {
        setShips(builder.ship);
        setName(builder.name);
        setUniverse(builder.universe);
        setImg(builder.img);
    }


    public static PlanetBuilder builder() {
        return new PlanetBuilder();
    }

    public static class PlanetBuilder {

        private List<Ship> ship;
        private Universe universe;
        private String img;
        private String name;


        /**
         * Empty constructor
         */
        public PlanetBuilder() {
        }

        public PlanetBuilder(List<Ship> ship, Universe universe, String img, String name) {
            this.ship = ship;
            this.universe = universe;
            this.img = img;
            this.name = name;

        }

        public PlanetBuilder ship(List<Ship> ship) {
            this.ship = ship;
            return PlanetBuilder.this;
        }

        public PlanetBuilder universe(Universe universe) {
            this.universe = universe;
            return PlanetBuilder.this;
        }

        public PlanetBuilder img(String img) {
            this.img = img;
            return PlanetBuilder.this;
        }

        public PlanetBuilder name(String name) {
            this.name = name;
            return PlanetBuilder.this;
        }


        public Planet build() {
            return new Planet(this);
        }

    }
}

