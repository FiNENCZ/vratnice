package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.HistorieSluzebniVozidlo;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.repository.HistorieSluzebniVozidloRepository;
import jakarta.transaction.Transactional;

@Service
public class HistorieSluzebniVozidloService {

    @Autowired
    private HistorieSluzebniVozidloRepository historieSluzebniVozidloRepository;

    @Transactional
    public HistorieSluzebniVozidlo create(HistorieSluzebniVozidlo historieSluzebniVozidlo) {
        return historieSluzebniVozidloRepository.save(historieSluzebniVozidlo);
    }

    public List<HistorieSluzebniVozidlo> findBySluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo) {
        return historieSluzebniVozidloRepository.findBySluzebniVozidlo(sluzebniVozidlo);
    }


}
