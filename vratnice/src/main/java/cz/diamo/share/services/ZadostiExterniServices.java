package cz.diamo.share.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.ZadostExterni;
import cz.diamo.share.entity.ZadostExterniZaznam;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.repository.ZadostExterniRepository;
import cz.diamo.share.repository.ZadostExterniZaznamRepository;

@Service
@TransactionalROE
public class ZadostiExterniServices {

    final static Logger logger = LogManager.getLogger(ZadostiExterniServices.class);

    @Autowired
    private ZadostExterniRepository zadostExterniRepository;

    @Autowired
    private ZadostExterniZaznamRepository zadostExterniZaznamRepository;

    public ZadostExterni getDetail(String idZadostExterni) {
        return zadostExterniRepository.getDetail(idZadostExterni);
    }

    @TransactionalWrite
    public ZadostExterni save(ZadostExterni zadostExterni, List<String> idZaznamy) throws UniqueValueException,
            NoSuchMessageException, RecordNotFoundException, ValidationException {

        zadostExterni.setCasZmn(Utils.getCasZmn());
        zadostExterni.setZmenuProvedl(Utils.getZmenuProv());

        zadostExterni = zadostExterniRepository.save(zadostExterni);

        if (idZaznamy != null) {
            for (String idZaznamu : idZaznamy) {
                if (!zadostExterniZaznamRepository.existsById(zadostExterni.getIdZadostExterni(), idZaznamu))
                    zadostExterniZaznamRepository.save(new ZadostExterniZaznam(zadostExterni, idZaznamu));
            }
        }

        return zadostExterni;
    }
}