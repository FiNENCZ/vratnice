package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.HistorieSluzebniVozidlo;
import cz.diamo.vratnice.entity.HistorieSluzebniVozidloAkce;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.enums.HistorieSluzebniVozidloAkceEnum;
import cz.diamo.vratnice.enums.SluzebniVozidloStavEnum;
import cz.diamo.vratnice.repository.HistorieSluzebniVozidloRepository;
import jakarta.transaction.Transactional;

@Service
public class HistorieSluzebniVozidloService {

    @Autowired
    private HistorieSluzebniVozidloRepository historieSluzebniVozidloRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;

    /**
     * Vytváří a ukládá záznam historie služebního vozidla na základě nového a
     * starého vozidla a uživatele.
     *
     * @param newSluzebniVozidlo Nové služební vozidlo, které se má uložit do
     *                           historie.
     * @param oldSluzebniVozidlo Staré služební vozidlo, které se porovnává s novým
     *                           vozidlem.
     * @param vratny             Uživatel, který provádí akci.
     * @return Uložený {@link HistorieSluzebniVozidlo} objekt.
     */
    @Transactional
    public HistorieSluzebniVozidlo create(SluzebniVozidlo newSluzebniVozidlo, SluzebniVozidlo oldSluzebniVozidlo,
            Uzivatel vratny) {
        HistorieSluzebniVozidlo historieSluzebniVozidlo = new HistorieSluzebniVozidlo();
        historieSluzebniVozidlo.setSluzebniVozidlo(newSluzebniVozidlo);

        if (oldSluzebniVozidlo.getIdSluzebniVozidlo() != null) {

            historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(
                    HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO));
            if (oldSluzebniVozidlo.getStav()
                    .getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE
                    && newSluzebniVozidlo.getStav()
                            .getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_AKTIVNI) {
                historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(
                        HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO));
            }
            if (oldSluzebniVozidlo.getStav()
                    .getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_AKTIVNI
                    && newSluzebniVozidlo.getStav()
                            .getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE) {
                historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(
                        HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO));
            }
            if (oldSluzebniVozidlo.getAktivita() && !newSluzebniVozidlo.getAktivita()) {
                historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(
                        HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO));
            }
            if (!oldSluzebniVozidlo.getAktivita() && newSluzebniVozidlo.getAktivita()) {
                historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(
                        HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO));
            }
        } else {
            historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(
                    HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO));
        }

        historieSluzebniVozidlo.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieSluzebniVozidlo.setUzivatel(vratny);

        return historieSluzebniVozidloRepository.save(historieSluzebniVozidlo);
    }

    /**
     * Vyhledává záznamy historie služebního vozidla podle zadaného služebního
     * vozidla.
     *
     * @param sluzebniVozidlo Služební vozidlo, podle kterého se vyhledávají záznamy
     *                        historie.
     * @return Seznam {@link HistorieSluzebniVozidlo} objektů odpovídajících
     *         zadanému služebnímu vozidlu.
     * @throws RecordNotFoundException Pokud není nalezeno žádné záznamy historie.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<HistorieSluzebniVozidlo> findBySluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo)
            throws RecordNotFoundException, NoSuchMessageException {
        List<HistorieSluzebniVozidlo> list = historieSluzebniVozidloRepository.findBySluzebniVozidlo(sluzebniVozidlo);

        if (list != null) {
            for (HistorieSluzebniVozidlo historieSluzebniVozidlo : list) {
                historieSluzebniVozidlo.getAkce().setNazev(resourcesComponent.getResources(
                        LocaleContextHolder.getLocale(), historieSluzebniVozidlo.getAkce().getNazevResx()));
            }
        }

        return list;
    }
}
