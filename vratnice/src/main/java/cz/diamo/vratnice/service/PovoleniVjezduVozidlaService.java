package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaRepository;
import jakarta.transaction.Transactional;

@Service
public class PovoleniVjezduVozidlaService {

    @Autowired
    private PovoleniVjezduVozidlaRepository povoleniVjezduVozidlaRepository;

    public List<PovoleniVjezduVozidla> getAll() {
        return povoleniVjezduVozidlaRepository.findAll();
    }

    public PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
    }

    public List<PovoleniVjezduVozidla> getByStav(String stav) {
        return povoleniVjezduVozidlaRepository.getByStav(stav);
    }

    @Transactional
    public PovoleniVjezduVozidla create(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.save(povoleniVjezduVozidla);
    }



}
