package cz.dp.share.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.entity.Databaze;
import cz.dp.share.repository.DatabazeRepository;

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
