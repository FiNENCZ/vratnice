package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.repository.NavstevniListekRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekService {

    @Autowired
    private NavstevniListekRepository navstevniListekRepository;

    @Transactional
    public NavstevniListek create(NavstevniListek navstevniListek) {
        return navstevniListekRepository.save(navstevniListek);
    }

    public List<NavstevniListek> getAll() {
        return navstevniListekRepository.findAll();
    }

    public NavstevniListek getDetail(String idNavstevniListek) {
        return navstevniListekRepository.getDetail(idNavstevniListek);
    }

    public List<NavstevniListek> getNavstevniListkyByUzivatel(Uzivatel uzivatel) {
        return navstevniListekRepository.findByUzivatel(uzivatel);
    }

    public List<NavstevniListek> getNavstevniListkyByNavstevaOsoba(NavstevaOsoba navstevaOsoba) {
        return navstevniListekRepository.findByNavstevaOsoba(navstevaOsoba);
    }

}
