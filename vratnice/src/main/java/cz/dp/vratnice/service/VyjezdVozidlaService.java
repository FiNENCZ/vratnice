package cz.dp.vratnice.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.dp.share.base.Utils;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.entity.VjezdVozidla;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.entity.VyjezdVozidla;
import cz.dp.vratnice.filter.FilterPristupuVratnice;
import cz.dp.vratnice.repository.VjezdVozidlaRepository;
import cz.dp.vratnice.repository.VyjezdVozidlaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class VyjezdVozidlaService {

    @Autowired
    private VyjezdVozidlaRepository vyjezdVozidlaRepository;

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Vrací seznam objektů {@link VyjezdVozidla} na základě zadaných filtrů.
     *
     * @param aktivita            Boolean hodnota.
     * @param nevyporadaneVyjezdy Boolean hodnota.
     * @param appUserDto          Objekt {@link AppUserDto} obsahující informace o
     *                            uživateli.
     * @return Seznam objektů {@link VyjezdVozidla} odpovídajících zadaným filtrům.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<VyjezdVozidla> getList(Boolean aktivita, Boolean nevyporadaneVyjezdy, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM VyjezdVozidla s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (nevyporadaneVyjezdy != null) {
            if (!nevyporadaneVyjezdy) {
                queryString.append("AND (s.zmenuProvedl <> 'kamery' AND s.zmenuProvedl IS NOT NULL) ");
            } else {
                queryString.append("AND (s.zmenuProvedl = 'kamery' OR s.zmenuProvedl IS NULL) ");
            }
        }

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<VyjezdVozidla> list = vysledek.getResultList();
        return list;
    }

    /**
     * Vrací detailní informace o objektu {@link VyjezdVozidla} na základě jeho ID.
     *
     * @param idVyjezdVozidla ID objektu {@link VyjezdVozidla}, jehož detail se má
     *                        vrátit.
     * @return Objekt {@link VyjezdVozidla} s detailními informacemi.
     */
    public VyjezdVozidla getDetail(String idVyjezdVozidla) {
        return vyjezdVozidlaRepository.getDetail(idVyjezdVozidla);
    }

    /**
     * Vrací seznam objektů {@link VyjezdVozidla} na základě registrační značky
     * vozidla.
     *
     * @param rzVozidla Registrační značka vozidla, podle které se mají vyhledat
     *                  výjezdy.
     * @return Seznam objektů {@link VyjezdVozidla} odpovídajících zadané
     *         registrační značce.
     */
    public List<VyjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    /**
     * Kontroluje, zda je možné vyjet s vozidlem na základě jeho registrační značky.
     *
     * @param rzVozidla Registrační značka vozidla, které se má zkontrolovat.
     * @return Volitelný objekt {@link VyjezdVozidla}, pokud je možné vyjet; jinak
     *         prázdný.
     */
    public Optional<VyjezdVozidla> jeMozneVyjet(String rzVozidla) {
        List<VjezdVozidla> vjezdVozidel = vjezdVozidlaRepository.getByRzVozidla(rzVozidla);
        List<VyjezdVozidla> vyjezdVozidel = vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    
        if (vjezdVozidel.isEmpty()) {
            return Optional.empty();
        }
    
        VjezdVozidla posledniVjezdVozidla = vjezdVozidel.get(vjezdVozidel.size() - 1);
    
        // Pokud neexistuje žádný záznam o výjezdu, může vozidlo vyjet
        if (vyjezdVozidel.isEmpty()) {
            return Optional.of(mapToVyjezdVozidla(posledniVjezdVozidla));
        }
    
        VyjezdVozidla posledniVyjezdVozidla = vyjezdVozidel.get(vyjezdVozidel.size() - 1);
    
        // Porovnání časů posledního vjezdu a výjezdu
        if (posledniVjezdVozidla.getCasPrijezdu().isAfter(posledniVyjezdVozidla.getCasOdjezdu())) {
            return Optional.of(mapToVyjezdVozidla(posledniVjezdVozidla));
        }
    
        return Optional.empty();
    }

    /**
     * Mapuje objekt {@link VjezdVozidla} na objekt {@link VyjezdVozidla}.
     *
     * @param posledniVjezdVozidla Objekt {@link VjezdVozidla}, který se má převést.
     * @return Nový objekt {@link VyjezdVozidla} s informacemi z posledního vjezdu.
     */
    private VyjezdVozidla mapToVyjezdVozidla(VjezdVozidla posledniVjezdVozidla) {
        VyjezdVozidla vyjezdVozidla = new VyjezdVozidla();

        if (posledniVjezdVozidla.getOpakovanyVjezd() != null) {
            vyjezdVozidla.setOpakovanyVjezd(true);
        }

        vyjezdVozidla.setRzVozidla(posledniVjezdVozidla.getRzVozidla());
        return vyjezdVozidla;
    }

    /**
     * Vytváří nový objekt {@link VyjezdVozidla} a ukládá ho do databáze.
     *
     * @param vyjezdVozidla Objekt {@link VyjezdVozidla}, který se má vytvořit.
     * @param vratnice      Objekt {@link Vratnice}, který se má přiřadit k výjezdu.
     * @return Uložený objekt {@link VyjezdVozidla} s aktualizovanými informacemi.
     */
    @Transactional
    public VyjezdVozidla create(VyjezdVozidla vyjezdVozidla, Vratnice vratnice) {
        if (vyjezdVozidla.getZmenuProvedl() == null) {
            vyjezdVozidla.setCasZmn(Utils.getCasZmn());
            vyjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        }

        if (vyjezdVozidla.getVratnice() == null)
            if (vratnice != null)
                vyjezdVozidla.setVratnice(vratnice);

        return vyjezdVozidlaRepository.save(vyjezdVozidla);
    }

    /**
     * Vytváří nový objekt {@link VyjezdVozidla} pro IZS a ukládá ho do databáze.
     *
     * @param rzVozidla Registrační značka vozidla, které se má přiřadit k novému
     *                  výjezdu.
     * @param vratnice  Objekt {@link Vratnice}, který se má přiřadit k výjezdu.
     * @return Uložený objekt {@link VyjezdVozidla} s aktualizovanými informacemi.
     */
    @Transactional
    public VyjezdVozidla createIZSVyjezdVozidla(String rzVozidla, Vratnice vratnice) {
        VyjezdVozidla vyjezdVozidlaIZS = new VyjezdVozidla();
        vyjezdVozidlaIZS.setRzVozidla(rzVozidla);
        vyjezdVozidlaIZS.setCasOdjezdu(ZonedDateTime.now());

        return create(vyjezdVozidlaIZS, vratnice);
    }

}
