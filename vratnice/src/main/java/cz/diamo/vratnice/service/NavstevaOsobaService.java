package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.repository.NavstevaOsobaRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevaOsobaService {

    @Autowired
    private NavstevaOsobaRepository navstevaOsobaRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SpolecnostService spolecnostService;

    @Transactional
    public NavstevaOsoba create(NavstevaOsoba navstevaOsoba) throws UniqueValueException, NoSuchMessageException {
        Spolecnost savedSpolecnost = spolecnostService.save(navstevaOsoba.getSpolecnost());
        navstevaOsoba.setSpolecnost((savedSpolecnost));

        if (navstevaOsoba.getIdNavstevaOsoba() == null || navstevaOsoba.getIdNavstevaOsoba().isEmpty()){
            if(navstevaOsobaRepository.existsByCisloOp(navstevaOsoba.getCisloOp())){
                throw new UniqueValueException(
                        messageSource.getMessage("navsteva_osoba.cislo_op.unique", null, LocaleContextHolder.getLocale()));
            }
        }
        return navstevaOsobaRepository.save(navstevaOsoba);
    }

    public List<NavstevaOsoba> list() {
        return navstevaOsobaRepository.findAll();
    }

    public NavstevaOsoba getDetail(String idNavstevaOsoba) {
        return navstevaOsobaRepository.getDetail(idNavstevaOsoba);
    }

    public NavstevaOsoba getByCisloOp(String cisloOp) {
        return navstevaOsobaRepository.geByCisloOp(cisloOp);
    }
}
