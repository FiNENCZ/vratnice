package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.exceptions.DuplicateCisloOpException;
import cz.diamo.vratnice.repository.NavstevaOsobaRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevaOsobaService {

    @Autowired
    private NavstevaOsobaRepository navstevaOsobaRepository;

    @Transactional
    public NavstevaOsoba create(NavstevaOsoba navstevaOsoba) {
        if (navstevaOsoba.getIdNavstevaOsoba() == null || navstevaOsoba.getIdNavstevaOsoba().isEmpty()){
            if(navstevaOsobaRepository.existsByCisloOp(navstevaOsoba.getCisloOp())){
                throw new DuplicateCisloOpException("Číslo OP musí být unikátní. V databázi již existuje osoba se stejným OP.");
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

    public NavstevaOsoba getRidicByCisloOp(String cisloOp) {
        return navstevaOsobaRepository.geByCisloOp(cisloOp);
    }



}
