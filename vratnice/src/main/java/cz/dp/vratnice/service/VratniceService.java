package cz.dp.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.base.Utils;
import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.repository.VratniceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class VratniceService {

    @Autowired
    private VratniceRepository vratniceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    /**
     * Vrací seznam objektů {@link Vratnice} na základě zadaných filtrů.
     *
     * @param aktivita   Boolean hodnota.
     * @param idLokalita ID lokality, podle které se mají vratnice filtrovat.
     * @return Seznam objektů {@link Vratnice} odpovídajících zadaným filtrům.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<Vratnice> getList(Boolean aktivita, String idLokalita)
            throws RecordNotFoundException, NoSuchMessageException {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Vratnice s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (idLokalita != null)
            queryString.append(" and s.lokalita.idLokalita = :idLokalita");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (idLokalita != null)
            vysledek.setParameter("idLokalita", idLokalita);

        @SuppressWarnings("unchecked")
        List<Vratnice> list = vysledek.getResultList();

        if (list != null) {
            for (Vratnice vratnice : list) {
                vratnice = translateVratnice(vratnice);
            }
        }

        return list;
    }

    /**
     * Ukládá objekt {@link Vratnice} do databáze.
     *
     * @param vratnice Objekt {@link Vratnice}, který se má uložit.
     * @return Uložený objekt {@link Vratnice} s aktualizovanými informacemi.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public Vratnice save(Vratnice vratnice) throws RecordNotFoundException, NoSuchMessageException {
        vratnice.setCasZmn(Utils.getCasZmn());
        vratnice.setZmenuProvedl(Utils.getZmenuProv());
        Vratnice savedVratnice = vratniceRepository.save(vratnice);
        return translateVratnice(savedVratnice);
    }

    /**
     * Vrací detailní informace o objektu {@link Vratnice} na základě jeho ID.
     *
     * @param id ID objektu {@link Vratnice}, jehož detail se má vrátit.
     * @return Objekt {@link Vratnice} s detailními informacemi.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam s daným ID.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Vratnice getDetail(String id) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratnice = vratniceRepository.getDetail(id);

        if (vratnice != null)
            vratnice = translateVratnice(vratnice);

        return vratnice;
    }

    /**
     * Převádí objekt {@link Vratnice} a aktualizuje jeho vlastnosti na základě
     * lokalizace.
     *
     * @param vratnice Objekt {@link Vratnice}, který se má převést.
     * @return Převáděný objekt {@link Vratnice} s aktualizovanými informacemi.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    private Vratnice translateVratnice(Vratnice vratnice) throws RecordNotFoundException, NoSuchMessageException {
        if (vratnice.getVstupniKartyTyp().getNazevResx() != null)
            vratnice.getVstupniKartyTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    vratnice.getVstupniKartyTyp().getNazevResx()));
        return vratnice;
    }
}
