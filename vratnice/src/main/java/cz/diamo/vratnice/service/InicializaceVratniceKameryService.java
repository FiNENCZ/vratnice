package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.InicializaceVratniceKamery;
import cz.diamo.vratnice.repository.InicializaceVratniceKameryRepository;
import jakarta.transaction.Transactional;

@Service
public class InicializaceVratniceKameryService {

    @Autowired
    private InicializaceVratniceKameryRepository vratniceKameryRepository;

    @Transactional
    public InicializaceVratniceKamery save(InicializaceVratniceKamery vratniceKamery) {
        InicializaceVratniceKamery inicializaceVratniceKameryOld = vratniceKameryRepository.getByIpAdresa(vratniceKamery.getIpAdresa());

        if(inicializaceVratniceKameryOld != null) {
            vratniceKamery.setIdInicializaceVratniceKamery(inicializaceVratniceKameryOld.getIdInicializaceVratniceKamery());
        }

        return vratniceKameryRepository.save(vratniceKamery);
    }

    public InicializaceVratniceKamery getByIpAdresa(String ipAdresa) {
        return vratniceKameryRepository.getByIpAdresa(ipAdresa);
    }

    public List<InicializaceVratniceKamery> list() {
        return vratniceKameryRepository.findAll();
    }

}
