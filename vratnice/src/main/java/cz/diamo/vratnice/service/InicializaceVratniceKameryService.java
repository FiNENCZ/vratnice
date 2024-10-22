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

    /**
     * Ukládá nebo aktualizuje instanci {@link InicializaceVratniceKamery}
     *
     * @param vratniceKamery Objekt {@link InicializaceVratniceKamery}, který se má
     *                       uložit nebo aktualizovat.
     * @return Uložený nebo aktualizovaný objekt {@link InicializaceVratniceKamery}.
     */
    @Transactional
    public InicializaceVratniceKamery save(InicializaceVratniceKamery vratniceKamery) {
        InicializaceVratniceKamery inicializaceVratniceKameryOld = vratniceKameryRepository
                .getByIpAdresa(vratniceKamery.getIpAdresa());

        if (inicializaceVratniceKameryOld != null) {
            vratniceKamery
                    .setIdInicializaceVratniceKamery(inicializaceVratniceKameryOld.getIdInicializaceVratniceKamery());
        }

        return vratniceKameryRepository.save(vratniceKamery);
    }

    /**
     * Vrací instanci {@link InicializaceVratniceKamery} na základě zadané IP
     * adresy.
     *
     * @param ipAdresa IP adresa, podle které se hledá objekt
     * @return Objekt {@link InicializaceVratniceKamery} odpovídající zadané IP
     *         adrese, nebo null, pokud nebyl nalezen.
     */
    public InicializaceVratniceKamery getByIpAdresa(String ipAdresa) {
        return vratniceKameryRepository.getByIpAdresa(ipAdresa);
    }

    /**
     * Vrací detailní informace o instanci {@link InicializaceVratniceKamery} na
     * základě jejího ID.
     *
     * @param id ID objektu {@link InicializaceVratniceKamery}, jehož detaily se
     *           mají vrátit.
     * @return Objekt {@link InicializaceVratniceKamery} odpovídající zadanému ID
     */
    public InicializaceVratniceKamery getDetail(String id) {
        return vratniceKameryRepository.getDetail(id);
    }

    /**
     * Vrací seznam všech instancí {@link InicializaceVratniceKamery}.
     *
     * @return Seznam objektů {@link InicializaceVratniceKamery}.
     */
    public List<InicializaceVratniceKamery> list() {
        return vratniceKameryRepository.findAll();
    }

    /**
     * Odstraňuje instanci {@link InicializaceVratniceKamery} na základě jejího ID.
     *
     * @param id ID objektu {@link InicializaceVratniceKamery}, který se má
     *           odstranit.
     */
    public void delete(String id) {
        vratniceKameryRepository.deleteById(id);
    }

}
