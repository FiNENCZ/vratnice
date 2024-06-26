package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.repository.VyjezdVozidlaRepository;
import jakarta.transaction.Transactional;

@Service
public class VyjezdVozidlaService {

    @Autowired
    private VyjezdVozidlaRepository vyjezdVozidlaRepository;

    public List<VyjezdVozidla> getAll() {
        return vyjezdVozidlaRepository.findAll();
    }

    public VyjezdVozidla getDetail(String idVyjezdVozidla) {
        return vyjezdVozidlaRepository.getDetail(idVyjezdVozidla);
    }

    public List<VyjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    @Transactional
    public VyjezdVozidla create(VyjezdVozidla vyjezdVozidla) {
        return vyjezdVozidlaRepository.save(vyjezdVozidla);
    }


}
