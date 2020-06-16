package de.bremen.server.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Planet extends StopAbstract {

    @NotNull
    @Column
    private String img;

    private String name;

    /**
     * Empty constructor
     */
    public Planet() {

    }

    public Planet(PlanetBuilder builder) {
        setShip(builder.ship);
        setUniverse(builder.universe);
        setImg(builder.img);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
