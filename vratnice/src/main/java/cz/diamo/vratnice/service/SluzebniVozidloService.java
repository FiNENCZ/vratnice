package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.repository.SluzebniVozidloRepository;
import jakarta.transaction.Transactional;

@Service
public class SluzebniVozidloService {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloService.class);

    @Autowired
    private SluzebniVozidloRepository sluzebniVozidloRepository;

    public List<SluzebniVozidlo> getAll() {
        return sluzebniVozidloRepository.findAll();
    }

    @Transactional
    public SluzebniVozidlo create(SluzebniVozidlo sluzebniVozidlo) {
        return sluzebniVozidloRepository.save(sluzebniVozidlo);
    }

    public SluzebniVozidlo getDetail(String id) {
        return sluzebniVozidloRepository.getDetail(id);
    }

    public List<SluzebniVozidlo> getSluzebniVozidloByStav(String stav) {
        return sluzebniVozidloRepository.getSluzebniVozidloByStav(stav);
    }

    public List<SluzebniVozidlo> getSluzebniVozidloByAktivita(Boolean aktivita) {
        return sluzebniVozidloRepository.findByAktivita(aktivita);
    }

}
