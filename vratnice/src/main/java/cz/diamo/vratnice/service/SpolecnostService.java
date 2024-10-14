package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.repository.SpolecnostRepository;

@Service
public class SpolecnostService {

    @Autowired
    private SpolecnostRepository spolecnostRepository;
    
    public Spolecnost save(Spolecnost spolecnost) {
        if (spolecnost.getIdSpolecnost() == null || spolecnost.getIdSpolecnost().isEmpty()) {
            if (spolecnostRepository.getByNazev(spolecnost.getNazev()) == null) {
                return spolecnostRepository.save(spolecnost);
            } else {
                return spolecnostRepository.getByNazev(spolecnost.getNazev());
            }
        }
        return spolecnost;
    }

    public List<Spolecnost> getList() {
        return spolecnostRepository.findAll();
    }

    public Spolecnost getByNazev(String nazev) {
        return spolecnostRepository.getByNazev(nazev);
    }

}
