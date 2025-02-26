package cz.dp.share.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.entity.ZadostExterni;
import cz.dp.share.entity.ZadostExterniZaznam;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.share.exceptions.ValidationException;
import cz.dp.share.repository.ZadostExterniRepository;
import cz.dp.share.repository.ZadostExterniZaznamRepository;

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