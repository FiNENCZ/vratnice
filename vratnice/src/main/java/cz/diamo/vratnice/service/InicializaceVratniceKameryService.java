package cz.diamo.vratnice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.InicializaceVratniceKamery;
import cz.diamo.vratnice.repository.InicializaceVratniceKameryRepository;

@Service
public class InicializaceVratniceKameryService {

    @Autowired
    private InicializaceVratniceKameryRepository vratniceKameryRepository;

    public InicializaceVratniceKamery save(InicializaceVratniceKamery vratniceKamery) {
        return vratniceKameryRepository.save(vratniceKamery);
    }

}
