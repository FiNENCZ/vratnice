package cz.diamo.vratnice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.repository.KlicRepository;
import jakarta.transaction.Transactional;

@Service
public class KlicService {

    @Autowired
    private KlicRepository keyRepository;

    public List<Klic> getAllKeys() {
        return keyRepository.findAll();
    }

    @Transactional
    public Klic createKey(Klic key) {
        return keyRepository.save(key);
    }

    public Klic getDetail(String idKey) {
        return keyRepository.getDetail(idKey);
    }

    public Klic getDetailByChipCode(String chipCode) {
        return keyRepository.getDetailByChipCode(chipCode);
    }

}
