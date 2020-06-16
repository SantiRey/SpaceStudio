package de.bremen.server.model;

import javax.persistence.*;

@Entity
public class Universe  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    public Universe(UniverseBuilder universeBuilder) {
        this.id=universeBuilder.id;
        this.name=universeBuilder.name;
    }

    public Universe() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static UniverseBuilder universeBuilder(){
        return new UniverseBuilder();
    }

    public static class UniverseBuilder{
        private Integer id;
        private String name;

        public UniverseBuilder id(Integer id){
            this.id=id;
            return UniverseBuilder.this;
        };
        public UniverseBuilder name(String name){
            this.name=name;
            return UniverseBuilder.this;
        };
        public Universe build(){
            return new Universe(this);
        }
    }
}