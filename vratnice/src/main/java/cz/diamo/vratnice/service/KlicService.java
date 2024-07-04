package cz.diamo.vratnice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.repository.KlicRepository;
import jakarta.transaction.Transactional;

@Service
public class KlicService {

    @Autowired
    private KlicRepository klicRepository;

    public List<Klic> getAllKeys() {
        return klicRepository.findAll();
    }

    @Transactional
    public Klic createKey(Klic klic) {
        klic.setCasZmn(Utils.getCasZmn());
        klic.setZmenuProvedl(Utils.getZmenuProv());
        return klicRepository.save(klic);
    }

    public Klic getDetail(String idKlic) {
        return klicRepository.getDetail(idKlic);
    }

    public Klic getDetailByChipCode(String kodCipu) {
        return klicRepository.getDetailByKodCipu(kodCipu);
    }

    public List<Klic> getBySpecialni(Boolean specialni) {
        return klicRepository.getBySpecialni(specialni);
    }


    public List<Klic> getKlicByAktivita(Boolean aktivita) {
        return klicRepository.findByAktivita(aktivita);
    }

}
