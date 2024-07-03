package cz.diamo.vratnice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<PovoleniVjezduVozidla> getByRzVozidla(String rzVozidla) {
        return povoleniVjezduVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public Optional<PovoleniVjezduVozidla> jeRzVozidlaPovolena(String rzVozidla) {
        List<PovoleniVjezduVozidla> povoleniVjezduVozidlaList = getByRzVozidla(rzVozidla);

        if (povoleniVjezduVozidlaList.isEmpty()) {
            return Optional.empty();
        }

        Date currentDate = new Date();
        for (PovoleniVjezduVozidla povoleniVjezduVozidla : povoleniVjezduVozidlaList) {
            if (currentDate.compareTo(povoleniVjezduVozidla.getDatumOd()) >= 0 
                    && currentDate.compareTo(povoleniVjezduVozidla.getDatumDo()) <= 0) {
                return Optional.of(povoleniVjezduVozidla);
            }
        }

        return Optional.empty();
    }

    @Transactional
    public PovoleniVjezduVozidla create(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.save(povoleniVjezduVozidla);
    }



}
