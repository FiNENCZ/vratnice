package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.NavstevaUzivatelStav;
import cz.diamo.vratnice.repository.NavstevaUzivatelStavRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevaUzivatelStavService {

    @Autowired
    private NavstevaUzivatelStavRepository navstevaUzivatelStavRepository;

    @Transactional
    public NavstevaUzivatelStav create(NavstevaUzivatelStav navstevaUzivatelStav) {
        return navstevaUzivatelStavRepository.save(navstevaUzivatelStav);
    }

    public List<NavstevaUzivatelStav> list() {
        return navstevaUzivatelStavRepository.findAll();
    }

}
