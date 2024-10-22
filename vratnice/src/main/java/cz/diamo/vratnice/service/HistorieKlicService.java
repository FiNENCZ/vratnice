package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.entity.HistorieKlic;
import cz.diamo.vratnice.entity.HistorieKlicAkce;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.enums.HistorieKlicAkceEnum;
import cz.diamo.vratnice.repository.HistorieKlicRepository;
import jakarta.transaction.Transactional;

@Service
public class HistorieKlicService {

    final static Logger logger = LogManager.getLogger(HistorieKlicService.class);

    @Autowired
    private HistorieKlicRepository historieKlicRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private UzivatelServices uzivatelServices;

    /**
     * Vytváří a ukládá záznam historie klíče na základě nového a starého klíče a
     * uživatele.
     *
     * @param newKlic
     * @param oldKlic
     * @param vratny  Uživatel, který provádí akci.
     * @return Uložený {@link HistorieKlic} objekt.
     */
    @Transactional
    public HistorieKlic create(Klic newKlic, Klic oldKlic, Uzivatel vratny) {
        HistorieKlic historieKlic = new HistorieKlic();
        historieKlic.setKlic(newKlic);

        if (oldKlic.getIdKlic() != null) {

            historieKlic.setAkce(new HistorieKlicAkce(HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_UPRAVEN));
            // if(oldKlic.getStav().getSluzebniVozidloStavEnum() ==
            // SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE
            // && newKlic.getStav().getSluzebniVozidloStavEnum() ==
            // SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_AKTIVNI){
            // historieKlic.setAkce(new
            // HistorieKlicAkce(HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_OBNOVEN));
            // }
            // if(oldKlic.getStav().getSluzebniVozidloStavEnum() ==
            // SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_AKTIVNI
            // && newKlic.getStav().getSluzebniVozidloStavEnum() ==
            // SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE){
            // historieKlic.setAkce(new
            // HistorieKlicAkce(HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_BLOKOVAN));
            // }
            if (oldKlic.getAktivita() && !newKlic.getAktivita()) {
                historieKlic.setAkce(new HistorieKlicAkce(HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_ODSTRANEN));
            }
            if (!oldKlic.getAktivita() && newKlic.getAktivita()) {
                historieKlic.setAkce(new HistorieKlicAkce(HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_OBNOVEN));
            }
        } else {
            historieKlic.setAkce(new HistorieKlicAkce(HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_VYTVOREN));
        }

        historieKlic.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieKlic.setUzivatel(vratny);

        return historieKlicRepository.save(historieKlic);
    }

    /**
     * Vytváří a ukládá záznam historie klíče na základě poskytnutého objektu
     * HistorieKlic a uživatelského DTO.
     *
     * @param historieKlic Objekt historie klíče, který se má uložit.
     * @param appUserDto   DTO uživatele, který provádí akci.
     * @return Uložený {@link HistorieKlic} objekt.
     * @throws RecordNotFoundException Pokud uživatel není nalezen.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public HistorieKlic createUzivatelem(HistorieKlic historieKlic, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel vratny = uzivatelServices.getDetail(appUserDto.getIdUzivatel());

        historieKlic.setUzivatel(vratny);
        historieKlic.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        return historieKlicRepository.save(historieKlic);

    }

    /**
     * Vrátí záznamy historie klíče podle ID klíče.
     *
     * @param idKlic ID klíče, podle kterého se vyhledávají záznamy historie.
     * @return Seznam {@link HistorieKlic} objektů odpovídajících zadanému ID klíče.
     * @throws Exception              Pokud dojde k chybě během vyhledávání.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    public List<HistorieKlic> findByKlic(String idKlic) throws Exception, NoSuchMessageException {
        List<HistorieKlic> list = historieKlicRepository.findByIdKlic(idKlic);

        if (list != null) {
            for (HistorieKlic historieKlic : list) {
                historieKlic.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        historieKlic.getAkce().getNazevResx()));
            }
        }

        return list;

    }
}
