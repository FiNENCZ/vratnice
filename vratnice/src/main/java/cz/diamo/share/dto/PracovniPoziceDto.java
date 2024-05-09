package cz.diamo.share.dto;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import cz.diamo.share.entity.PracovniPozice;
import cz.diamo.share.entity.PracovniPozicePrehled;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PracovniPoziceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String sapId;

    private String sapIdNadrizeny;

    private String zkratka;

    private String nazev;

    private String uzivatelSapId;

    private String uzivatelNazev;

    private String prijmeni;

    private String jmeno;

    private String email;

    private String tel;

    private String zavodNazev;

    private String zavodSapId;

    private String fullText;

    public void pridatDoFullText(String value) {
        if (StringUtils.isBlank(value))
            return;
        if (getFullText() == null)
            setFullText("");

        if (!StringUtils.isBlank(value))
            setFullText(getFullText() + " ");
        setFullText(getFullText() + value);
    }

    public PracovniPoziceDto(PracovniPozice pracovniPozice) {
        if (pracovniPozice == null)
            return;

        setId(pracovniPozice.getIdPracovniPozice());
        setSapId(pracovniPozice.getSapId());
        setSapIdNadrizeny(pracovniPozice.getSapIdNadrizeny());
        setZkratka(pracovniPozice.getZkratka());
        setNazev(pracovniPozice.getNazev());

        vyplnitFullText();
    }

    public PracovniPoziceDto(PracovniPozicePrehled pracovniPozicePrehled) {
        if (pracovniPozicePrehled == null)
            return;

        setId(pracovniPozicePrehled.getIdPracovniPozice());
        setSapId(pracovniPozicePrehled.getSapId());
        setSapIdNadrizeny(pracovniPozicePrehled.getSapIdNadrizeny());
        setZkratka(pracovniPozicePrehled.getZkratka());
        setNazev(pracovniPozicePrehled.getNazev());
        setUzivatelSapId(pracovniPozicePrehled.getUzivatelSapId());
        setUzivatelNazev(pracovniPozicePrehled.getUzivatelNazev());
        setPrijmeni(pracovniPozicePrehled.getPrijmeni());
        setJmeno(pracovniPozicePrehled.getJmeno());
        setEmail(pracovniPozicePrehled.getEmail());
        setTel(pracovniPozicePrehled.getTel());
        setZavodNazev(pracovniPozicePrehled.getZavodNazev());
        setZavodSapId(pracovniPozicePrehled.getZavodSapId());

        vyplnitFullText();
    }

    public void vyplnitFullTextPotomka(PracovniPoziceDto potomek) {
        pridatDoFullText(potomek.getFullText());
    }

    private void vyplnitFullText() {
        pridatDoFullText(getSapId());
        pridatDoFullText(getZkratka());
        pridatDoFullText(getNazev());
        pridatDoFullText(getUzivatelSapId());
        pridatDoFullText(getPrijmeni());
        pridatDoFullText(getJmeno());
        pridatDoFullText(getUzivatelNazev());
        pridatDoFullText(getEmail());
        pridatDoFullText(getTel());
        pridatDoFullText(getZavodNazev());
    }

    public PracovniPozice getPracovniPozice(PracovniPozice pracovniPozice, boolean pouzeId) {
        if (pracovniPozice == null)
            pracovniPozice = new PracovniPozice();

        pracovniPozice.setIdPracovniPozice(getId());
        if (!pouzeId) {
            pracovniPozice.setZkratka(getZkratka());
            pracovniPozice.setNazev(getNazev());
        }

        return pracovniPozice;
    }

    public PracovniPozicePrehled getPracovniPozicePrehled(PracovniPozicePrehled pracovniPozicePrehled) {
        if (pracovniPozicePrehled == null)
            pracovniPozicePrehled = new PracovniPozicePrehled();

        pracovniPozicePrehled.setIdPracovniPozice(getId());
        return pracovniPozicePrehled;
    }
}
