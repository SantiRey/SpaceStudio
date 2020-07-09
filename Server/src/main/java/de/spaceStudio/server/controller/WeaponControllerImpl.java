package de.spaceStudio.server.controller;

import de.spaceStudio.server.model.Section;
import de.spaceStudio.server.model.SectionTyp;
import de.spaceStudio.server.model.Ship;
import de.spaceStudio.server.model.Weapon;
import de.spaceStudio.server.repository.SectionRepository;
import de.spaceStudio.server.repository.ShipRepository;
import de.spaceStudio.server.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WeaponControllerImpl implements WeaponController {
    @Autowired
    WeaponRepository weaponRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    ShipRepository shipRepository;
    @Override
    public boolean canAttack(Weapon w, Ship s) {
        return false;
    }

    @Override
    public Ship calculateDammage(Weapon w, Section sec, Ship s) {
        return null;
    }

    @Override
    @RequestMapping(value = "/weapons", method = RequestMethod.GET)
    public List<Weapon> getAllWeapons() {
        return weaponRepository.findAll();
    }

    @Override
    @RequestMapping(value = "/weapon/{id}", method = RequestMethod.GET)
    public Weapon getWeapon(@PathVariable Integer id) {
        return weaponRepository.getOne(id);
    }

    @Override
    @RequestMapping(value = "/weapon", method = RequestMethod.POST)

    public String addWeapon(@RequestBody Weapon weapon) {
        Ship ship=weapon.getSection().getShip();
        ship=shipRepository.findShipByName(ship.getName()).get();
        List<Section> sections=sectionRepository.findAllByShip(ship).get();
        for(Section s:sections){
            if(s.getSectionTyp().equals(SectionTyp.WEAPONS)){
                weapon.setSection(s);
                break;
            }
        }
        weaponRepository.save(weapon);
        return HttpStatus.CREATED.toString();    }

    @Override
    @RequestMapping(value = "/weapon", method = RequestMethod.PUT)

    public Weapon updateWeapon(@RequestBody Weapon weapon) {
        Weapon weapon1 = weaponRepository.save(weapon);
        return weapon1;
    }

    @Override
    @RequestMapping(value = "/weapon/{id}", method = RequestMethod.DELETE)

    public String deleteWeaponById(@PathVariable Integer id) {
        weaponRepository.deleteById(id);
        return HttpStatus.ACCEPTED.toString();
    }

    @Override
    @RequestMapping(value = "/weapons", method = RequestMethod.DELETE)

    public String deleteAllWeapons() {
        weaponRepository.deleteAll();
        return HttpStatus.OK.toString();
    }

}
