package cz.dp.share.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.UzivatelskeNastaveni;
import cz.dp.share.repository.UzivatelskeNastaveniRepository;

@Service
@TransactionalROE
public class UzivatelskeNastaveniServices {

    @Autowired
    private UzivatelskeNastaveniRepository uzivatelskeNastaveniRepository;

    public String getDetail(String idUzivatel, String klic) {
        UzivatelskeNastaveni uzivatelskeNastaveni = uzivatelskeNastaveniRepository.getDetail(idUzivatel, klic.trim());
        if (uzivatelskeNastaveni == null) {
            return null;
        } else {
            return uzivatelskeNastaveni.getHodnota();
        }
    }

    @TransactionalWrite
    public void save(String idUzivatel, String klic, String hodnota) {
        UzivatelskeNastaveni uzivatelskeNastaveni = uzivatelskeNastaveniRepository.getDetail(idUzivatel, klic.trim());
        if (uzivatelskeNastaveni == null) {
            uzivatelskeNastaveni = new UzivatelskeNastaveni();
            Uzivatel uzivatel = new Uzivatel();
            uzivatel.setIdUzivatel(idUzivatel);
            uzivatelskeNastaveni.setUzivatel(uzivatel);
            uzivatelskeNastaveni.setKlic(klic.trim());
        }
        uzivatelskeNastaveni.setHodnota(hodnota.trim());
        uzivatelskeNastaveniRepository.save(uzivatelskeNastaveni);
    }

    @TransactionalWrite
    public void delete(String idUzivatel, String klic) {
        uzivatelskeNastaveniRepository.delete(idUzivatel, klic);
    }

    @TransactionalWrite
    public void delete(String idUzivatel) {
        uzivatelskeNastaveniRepository.deleteAll(idUzivatel);
    }

}
