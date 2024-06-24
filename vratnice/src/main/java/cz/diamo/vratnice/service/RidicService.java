package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.exceptions.DuplicateCisloOpException;
import cz.diamo.vratnice.repository.RidicRepository;
import jakarta.transaction.Transactional;

@Service
public class RidicService {

    @Autowired
    private RidicRepository ridicRepository;

    @Transactional
    public Ridic create(Ridic ridic) {
        if (ridic.getIdRidic() == null || ridic.getIdRidic().isEmpty()){
            if(ridicRepository.existsByCisloOp(ridic.getCisloOp())){
                throw new DuplicateCisloOpException("Číslo OP musí být unikátní. V databázi již existuje řidič se stejným OP.");
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
