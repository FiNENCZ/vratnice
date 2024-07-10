package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.repository.LokalitaRepository;

@Service
public class LokalitaService {

    @Autowired
    private LokalitaRepository lokalitaRepository;

    public List<Lokalita> list() {
        return lokalitaRepository.findAll();
    }

    public Lokalita detail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

}
