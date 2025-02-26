package cz.dp.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.base.Utils;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Wso2UzivatelExtDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "{sapid.max.100}")
    private String sapId;

    @NotNull(message = "{zavod.require}")
    private Wso2ZavodDto zavod;

    @Size(max = 1000, message = "{nazev.max.1000}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    private String jmeno;

    private String prijmeni;

    private String email;

    private String tel;

    @NotNull(message = "{datum.od.require}")
    private Date datumOd;

    private Date datumDo;

    private String poznamka;

    private String rfid1;

    private String rfid2;

    private List<Wso2ZavodDto> ostatniZavody;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    public Wso2UzivatelExtDto(Uzivatel uzivatel) {
        setSapId(uzivatel.getSapId());
        setZavod(new Wso2ZavodDto(uzivatel.getZavod()));
        setNazev(uzivatel.getNazev());
        setJmeno(uzivatel.getJmeno());
        setPrijmeni(uzivatel.getPrijmeni());
        setEmail(uzivatel.getEmail());
        setTel(uzivatel.getTel());
        setDatumOd(uzivatel.getDatumOd());
        setDatumDo(uzivatel.getDatumDo());
        setPoznamka(uzivatel.getPoznamka());
        setRfid1(uzivatel.getCip1());
        setRfid2(uzivatel.getCip2());
        setOstatniZavody(new ArrayList<Wso2ZavodDto>());
        if (uzivatel.getOstatniZavody() != null) {
            for (Zavod zavod : uzivatel.getOstatniZavody()) {
                getOstatniZavody().add(new Wso2ZavodDto(zavod));
            }
        }
        setAktivita(uzivatel.getAktivita());

    }

    @JsonIgnore
    public Uzivatel getUzivatel(Uzivatel uzivatel) {

        if (uzivatel == null) {
            uzivatel = new Uzivatel();
            uzivatel.setZmena(true);
            uzivatel.setExterni(true);
            uzivatel.setPruznaPracDoba(true);
            uzivatel.setZavod(new Zavod());
        }

        if (!StringUtils.equals(getSapId(), uzivatel.getSapId())) {
            uzivatel.setSapId(getSapId());
            uzivatel.setZmena(true);
        }

        if (!StringUtils.equals(getZavod().getSapId(), uzivatel.getZavod().getSapId())) {
            uzivatel.getZavod().setSapId(getZavod().getSapId());
            uzivatel.setZmena(true);
        }
        if (!StringUtils.equals(getNazev(), uzivatel.getNazev())) {
            uzivatel.setNazev(getNazev());
            uzivatel.setZmena(true);
        }
        if (!StringUtils.equals(getJmeno(), uzivatel.getJmeno())) {
            uzivatel.setJmeno(getJmeno());
            uzivatel.setZmena(true);
        }
        if (!StringUtils.equals(getPrijmeni(), uzivatel.getPrijmeni())) {
            uzivatel.setPrijmeni(getPrijmeni());
            uzivatel.setZmena(true);
        }
        if (!StringUtils.equals(getEmail(), uzivatel.getEmail())) {
            uzivatel.setEmail(getEmail());
            uzivatel.setZmena(true);
        }
        if (!StringUtils.equals(getTel(), uzivatel.getTel())) {
            uzivatel.setTel(getTel());
            uzivatel.setZmena(true);
        }

        if (!Utils.stejnyDen(getDatumOd(), uzivatel.getDatumOd(), true)) {
            uzivatel.setDatumOd(getDatumOd());
            uzivatel.setZmena(true);
        }

        if (!Utils.stejnyDen(getDatumDo(), uzivatel.getDatumDo(), true)) {
            uzivatel.setDatumDo(getDatumDo());
            uzivatel.setZmena(true);
        }

        if (!StringUtils.equals(getPoznamka(), uzivatel.getPoznamka())) {
            uzivatel.setPoznamka(getPoznamka());
            uzivatel.setZmena(true);
        }

        if (!StringUtils.equals(getRfid1(), uzivatel.getCip1())) {
            uzivatel.setCip1(getRfid1());
            uzivatel.setZmena(true);
        }

        if (!StringUtils.equals(getRfid2(), uzivatel.getCip2())) {
            uzivatel.setCip2(getRfid2());
            uzivatel.setZmena(true);
        }

        if (getOstatniZavody() != null && getOstatniZavody().size() > 0 && uzivatel.getOstatniZavody() != null
                && uzivatel.getOstatniZavody().size() == getOstatniZavody().size()) {
            for (Wso2ZavodDto wso2Zavod : getOstatniZavody()) {
                boolean dohledano = false;
                for (Zavod zavod : uzivatel.getOstatniZavody()) {
                    if (zavod.getSapId().equals(wso2Zavod.getSapId())) {
                        dohledano = true;
                        break;
                    }
                }
                if (!dohledano) {
                    uzivatel.setZmena(true);
                    break;
                }
            }
        } else if (getOstatniZavody() != null || uzivatel.getOstatniZavody() != null)
            uzivatel.setZmena(true);

        if (uzivatel.isZmena()) {
            uzivatel.setOstatniZavody(new ArrayList<Zavod>());
            if (getOstatniZavody() != null) {
                for (Wso2ZavodDto wso2Zavod : getOstatniZavody()) {
                    Zavod zavod = new Zavod();
                    zavod.setSapId(wso2Zavod.getSapId());
                    uzivatel.getOstatniZavody().add(zavod);
                }
            }
        }

        return uzivatel;
    }
}
