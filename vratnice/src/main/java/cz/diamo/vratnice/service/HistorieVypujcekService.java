package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.repository.HistorieVypujcekRepository;
import jakarta.transaction.Transactional;

@Service
public class HistorieVypujcekService {

    @Autowired
    private HistorieVypujcekRepository historieVypujcekRepository;

    @Transactional
    public HistorieVypujcek create(HistorieVypujcek historieVypujcek) {
        return historieVypujcekRepository.save(historieVypujcek);
    }

    public List<HistorieVypujcek> findByZadostKlic(ZadostKlic zadostKlic) {
        return historieVypujcekRepository.findByZadostKlic(zadostKlic);
    }

}
