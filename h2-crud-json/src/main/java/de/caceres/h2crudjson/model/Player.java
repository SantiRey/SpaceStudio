package de.caceres.h2crudjson.model;

import javax.persistence.*;
import java.util.List;

/**
 * @author Miguel Caceres 09.05.2020
 */
@Entity
@Table(name = "Player")
public class Player extends Actor{

	private String name;

	private String password;

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

}