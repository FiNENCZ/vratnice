package cz.diamo.share.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.Databaze;
import cz.diamo.share.repository.DatabazeRepository;

@Service
@Transactional(readOnly = true)
public class DatabazeServices {

    @Autowired
    private DatabazeRepository databazeRepository;

    public Databaze getDetail() {
        // v tabulce je vždy jen jeden záznam s ID = 0
        Databaze databaze = databazeRepository.findById(0).get();
        return databaze;
    }

    @TransactionalWrite
    private Databaze save(Databaze databaze) {
        databaze = databazeRepository.save(databaze);
        return databaze;
    }

    @TransactionalWrite
    public Databaze saveKcUzivatele(String userName, String password) {
        Databaze databaze = getDetail();
        databaze.setKcUzivateleJmeno(userName);
        databaze.setKcUzivateleHeslo(Utils.textEncrypted(password));
        return save(databaze);
    }

}
