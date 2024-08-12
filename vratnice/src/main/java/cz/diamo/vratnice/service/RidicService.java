package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.repository.RidicRepository;
import jakarta.transaction.Transactional;

@Service
public class RidicService {

    @Autowired
    private RidicRepository ridicRepository;

    @Autowired
    private MessageSource messageSource;

    @Transactional
    public Ridic create(Ridic ridic) throws UniqueValueException, NoSuchMessageException {
        if (ridic.getIdRidic() == null || ridic.getIdRidic().isEmpty()){
            if(ridicRepository.existsByCisloOp(ridic.getCisloOp())){
                throw new UniqueValueException(
                        messageSource.getMessage("ridic.cisloOp.unique", null, LocaleContextHolder.getLocale()));
            }
        }
        return ridicRepository.save(ridic);
    }

    public List<Ridic> list() {
        return ridicRepository.findAll();
    }

    public Ridic getDetail(String idRidic) {
        return ridicRepository.getDetail(idRidic);
    }

    public Ridic getRidicByCisloOp(String cisloOp) {
        return ridicRepository.getRidicByCisloOp(cisloOp);
    }


}
