package cz.diamo.vratnice.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.NajemnikNavstevnickaKarta;
import cz.diamo.vratnice.exceptions.DuplicateCisloOpException;
import cz.diamo.vratnice.repository.NajemnikNavstevnickaKartaRepository;
import jakarta.transaction.Transactional;

@Service
public class NajemnikNavstevnickaKartaService {

    @Autowired
    private NajemnikNavstevnickaKartaRepository najemnikNavstevnickaKartaRepository;

    @Transactional
    public NajemnikNavstevnickaKarta create(NajemnikNavstevnickaKarta najemnikNavstevnickaKarta) {
        if (najemnikNavstevnickaKarta.getIdNajemnikNavstevnickaKarta() == null || najemnikNavstevnickaKarta.getIdNajemnikNavstevnickaKarta().isEmpty()){
            if(najemnikNavstevnickaKartaRepository.existsByCisloOp(najemnikNavstevnickaKarta.getCisloOp())){
                throw new DuplicateCisloOpException("Číslo OP musí být unikátní. V databázi již existuje nájemník se stejným OP.");
            }
        }
        najemnikNavstevnickaKarta.setCasZmn(Utils.getCasZmn());
        najemnikNavstevnickaKarta.setZmenuProvedl(Utils.getZmenuProv());
        return najemnikNavstevnickaKartaRepository.save(najemnikNavstevnickaKarta);
    }

    public List<NajemnikNavstevnickaKarta> list() {
        return najemnikNavstevnickaKartaRepository.findAll();
    }

    public NajemnikNavstevnickaKarta getDetail(String idNajemnikNavstevnickaKarta) {
        return najemnikNavstevnickaKartaRepository.getDetail(idNajemnikNavstevnickaKarta);
    }

    public NajemnikNavstevnickaKarta getByCisloOp(String cisloOp) {
        return najemnikNavstevnickaKartaRepository.getByCisloOp(cisloOp);
    }

}
