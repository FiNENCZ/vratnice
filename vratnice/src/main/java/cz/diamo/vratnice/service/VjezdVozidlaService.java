package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import jakarta.transaction.Transactional;

@Service
public class VjezdVozidlaService {

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    public List<VjezdVozidla> getAll() {
        return vjezdVozidlaRepository.findAll();
    }

    public VjezdVozidla getDetail(String idVjezdVozidla) {
        return vjezdVozidlaRepository.getDetail(idVjezdVozidla);
    }

    public List<VjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public List<VjezdVozidla> getByRidic(Ridic ridic) {
        return vjezdVozidlaRepository.getByRidic(ridic);
    }

    @Transactional
    public VjezdVozidla create(VjezdVozidla vjezdVozidla) {
        return vjezdVozidlaRepository.save(vjezdVozidla);
    }

}
