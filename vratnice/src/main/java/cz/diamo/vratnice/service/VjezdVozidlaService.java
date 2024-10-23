package cz.diamo.vratnice.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.enums.VozidloTypEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class VjezdVozidlaService {

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    /**
     * Vrací seznam objektů {@link VjezdVozidla} na základě zadaných filtrů.
     *
     * @param aktivita           Boolean hodnota.
     * @param nevyporadaneVjezdy Boolean hodnota.
     * @param appUserDto         Objekt {@link AppUserDto} obsahující informace o
     *                           uživateli.
     * @return Seznam objektů {@link VjezdVozidla} odpovídajících zadaným filtrům.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<VjezdVozidla> getList(Boolean aktivita, Boolean nevyporadaneVjezdy, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM VjezdVozidla s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (nevyporadaneVjezdy != null) {
            if (!nevyporadaneVjezdy) {
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
        List<VjezdVozidla> list = vysledek.getResultList();

        if (list != null) {
            for (VjezdVozidla vjezdVozidla : list) {
                vjezdVozidla = translateVjezdVozidla(vjezdVozidla);
            }
        }

        return list;
    }

    /**
     * Převádí objekt {@link VjezdVozidla} a aktualizuje jeho vlastnosti na základě
     * lokalizace.
     *
     * @param vjezdVozidla Objekt {@link VjezdVozidla}, který se má převést.
     * @return Převáděný objekt {@link VjezdVozidla} s aktualizovanými informacemi.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    private VjezdVozidla translateVjezdVozidla(VjezdVozidla vjezdVozidla)
            throws RecordNotFoundException, NoSuchMessageException {
        if (vjezdVozidla.getTypVozidla() != null && vjezdVozidla.getTypVozidla().getNazevResx() != null)
            vjezdVozidla.getTypVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    vjezdVozidla.getTypVozidla().getNazevResx()));

        return vjezdVozidla;
    }

    /**
     * Vrací detailní informace o objektu {@link VjezdVozidla} na základě jeho ID.
     *
     * @param idVjezdVozidla ID objektu {@link VjezdVozidla}, jehož detail se má
     *                       vrátit.
     * @return Objekt {@link VjezdVozidla} s detailními informacemi.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam s daným ID.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public VjezdVozidla getDetail(String idVjezdVozidla) throws RecordNotFoundException, NoSuchMessageException {

        VjezdVozidla vjezdVozidla = vjezdVozidlaRepository.getDetail(idVjezdVozidla);

        if (vjezdVozidla != null)
            vjezdVozidla = translateVjezdVozidla(vjezdVozidla);

        return vjezdVozidla;
    }

    /**
     * Vytváří nový objekt {@link VjezdVozidla} a ukládá ho do databáze.
     *
     * @param vjezdVozidla Objekt {@link VjezdVozidla}, který se má vytvořit.
     * @param vratnice     Objekt {@link Vratnice}, který se má přiřadit k vjezdu.
     * @return Uložený objekt {@link VjezdVozidla} s aktualizovanými informacemi.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public VjezdVozidla create(VjezdVozidla vjezdVozidla, Vratnice vratnice)
            throws RecordNotFoundException, NoSuchMessageException {
        if (vjezdVozidla.getZmenuProvedl() == null) {
            vjezdVozidla.setCasZmn(Utils.getCasZmn());
            vjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        }

        if (vjezdVozidla.getVratnice() == null)
            if (vratnice != null)
                vjezdVozidla.setVratnice(vratnice);

        VjezdVozidla saveVjezdVozidla = vjezdVozidlaRepository.save(vjezdVozidla);
        return translateVjezdVozidla(saveVjezdVozidla);
    }

    /**
     * Vytváří nový objekt {@link VjezdVozidla} pro IZS a ukládá ho do databáze.
     *
     * @param rzVozidla RZ vozidla, které se má přiřadit k novému vjezdu.
     * @param vratnice  Objekt {@link Vratnice}, který se má přiřadit k vjezdu.
     * @return Uložený objekt {@link VjezdVozidla} s aktualizovanými informacemi.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public VjezdVozidla createIZSVjezdVozidla(String rzVozidla, Vratnice vratnice)
            throws RecordNotFoundException, NoSuchMessageException {
        VjezdVozidla vjezdVozidlaIZS = new VjezdVozidla();
        vjezdVozidlaIZS.setRzVozidla(rzVozidla);
        vjezdVozidlaIZS.setCasPrijezdu(ZonedDateTime.now());
        vjezdVozidlaIZS.setTypVozidla(new VozidloTyp(VozidloTypEnum.VOZIDLO_IZS));

        return create(vjezdVozidlaIZS, vratnice);
    }
}
