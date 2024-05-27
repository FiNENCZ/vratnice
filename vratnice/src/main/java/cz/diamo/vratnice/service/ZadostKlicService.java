package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.repository.ZadostKlicRepository;
import jakarta.transaction.Transactional;

@Service
public class ZadostKlicService {

    @Autowired
    private ZadostKlicRepository zadostiKlicRepository;

    public List<ZadostKlic> getAll() {
        return zadostiKlicRepository.findAll();
    }

    @Transactional
    public ZadostKlic create(ZadostKlic zadostKlic) {
        return zadostiKlicRepository.save(zadostKlic);
    }

    public ZadostKlic getDetail(String idZadostiKlic) {
        return zadostiKlicRepository.getDetail(idZadostiKlic);
    }

}
