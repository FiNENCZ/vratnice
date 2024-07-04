package cz.diamo.vratnice.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.NajemnikNavstevnickaKarta;
import cz.diamo.vratnice.exceptions.DuplicateCisloOpException;
import cz.diamo.vratnice.repository.NajemnikNavstevnickaKartaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class NajemnikNavstevnickaKartaService {

    @Autowired
    private NajemnikNavstevnickaKartaRepository najemnikNavstevnickaKartaRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    public List<NajemnikNavstevnickaKarta> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from NajemnikNavstevnickaKarta s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<NajemnikNavstevnickaKarta> list = vysledek.getResultList();
        return list;
    }

    public NajemnikNavstevnickaKarta getDetail(String idNajemnikNavstevnickaKarta) {
        return najemnikNavstevnickaKartaRepository.getDetail(idNajemnikNavstevnickaKarta);
    }

    public NajemnikNavstevnickaKarta getByCisloOp(String cisloOp) {
        return najemnikNavstevnickaKartaRepository.getByCisloOp(cisloOp);
    }

}
