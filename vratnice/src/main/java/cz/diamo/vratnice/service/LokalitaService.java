package cz.diamo.vratnice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.repository.LokalitaRepository;

@Service
public class LokalitaService {

    @Autowired
    private LokalitaRepository lokalitaRepository;

    public Lokalita getDetail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

}
