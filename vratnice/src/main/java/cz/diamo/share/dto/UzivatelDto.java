package cz.diamo.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.dto.opravneni.OpravneniDto;
import cz.diamo.share.dto.opravneni.RoleDto;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.PracovniPozice;
import cz.diamo.share.entity.Role;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zakazka;
import cz.diamo.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UzivatelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @Size(max = 100, message = "{sapid.max.100}")
    private String sapId;

    private ZavodDto zavod;

    private ZakazkaDto zakazka;

    private PracovniPoziceDto pracovniPozice;

    @Size(max = 1000, message = "{nazev.max.1000}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    private String jmeno;

    private String prijmeni;

    private String titulPred;

    private String titulZa;

    private String email;

    private String soukromyEmail;

    private String tel;

    private Date datumOd;

    private Date datumDo;

    private String poznamka;

    private Date platnostKeDni;

    private Date casAktualizace;

    private Boolean ukonceno;

    private Boolean pruznaPracDoba;

    private String rfid1;

    private String rfid2;

    private List<RoleDto> role;

    private List<OpravneniDto> opravneni;

    private List<ZavodDto> ostatniZavody;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    private Boolean varovani;

    private String varovaniText;

    public String getNazevSapId() {
        return String.format("%s (%s)", getNazev(), getSapId());
    }

    public UzivatelDto(Uzivatel uzivatel) {
        if (uzivatel == null)
            return;

        setId(uzivatel.getIdUzivatel());
        setSapId(uzivatel.getSapId());
        setZavod(new ZavodDto(uzivatel.getZavod()));
        setPracovniPozice(new PracovniPoziceDto(uzivatel.getPracovniPozice()));
        setNazev(uzivatel.getNazev());
        setJmeno(uzivatel.getJmeno());
        setPrijmeni(uzivatel.getPrijmeni());
        setTitulPred(uzivatel.getTitulPred());
        setTitulZa(uzivatel.getTitulZa());
        setEmail(uzivatel.getEmail());
        setSoukromyEmail(uzivatel.getSoukromyEmail());
        setTel(uzivatel.getTel());
        setDatumOd(uzivatel.getDatumOd());
        setDatumDo(uzivatel.getDatumDo());
        setPoznamka(uzivatel.getPoznamka());
        setPlatnostKeDni(uzivatel.getPlatnostKeDni());
        setCasAktualizace(uzivatel.getCasAktualizace());
        setUkonceno(uzivatel.getUkonceno());
        setPruznaPracDoba(uzivatel.getPruznaPracDoba());
        setRfid1(uzivatel.getCip1());
        setRfid2(uzivatel.getCip2());
        setAktivita(uzivatel.getAktivita());

        if (uzivatel.getZakazka() != null)
            setZakazka(new ZakazkaDto(uzivatel.getZakazka()));

        List<OpravneniDto> opravneniDtos = new ArrayList<>();
        if (uzivatel.getOpravneni() != null) {
            for (Opravneni opravneni : uzivatel.getOpravneni()) {
                opravneniDtos.add(new OpravneniDto(opravneni, null, true));
            }
        }
        setOpravneni(opravneniDtos);

        List<RoleDto> roleDtos = new ArrayList<>();
        if (uzivatel.getRole() != null) {
            for (Role role : uzivatel.getRole()) {
                roleDtos.add(new RoleDto(role));
            }
        }
        setRole(roleDtos);

        List<ZavodDto> zavodDtos = new ArrayList<>();
        if (uzivatel.getOstatniZavody() != null) {
            for (Zavod zavod : uzivatel.getOstatniZavody()) {
                zavodDtos.add(new ZavodDto(zavod));
            }
        }
        setOstatniZavody(zavodDtos);
    }

    @JsonIgnore
    public Uzivatel getUzivatel(Uzivatel uzivatel, boolean pouzeId) {
        if (uzivatel == null)
            uzivatel = new Uzivatel();

        uzivatel.setIdUzivatel(getId());
        if (!pouzeId) {
            uzivatel.setSapId(getSapId());
            uzivatel.setZavod(new Zavod(getZavod().getId()));
            uzivatel.setPracovniPozice(new PracovniPozice(getPracovniPozice().getId()));
            uzivatel.setNazev(getNazev());
            uzivatel.setJmeno(getJmeno());
            uzivatel.setPrijmeni(getPrijmeni());
            uzivatel.setTitulPred(getTitulPred());
            uzivatel.setTitulZa(getTitulZa());
            uzivatel.setEmail(getEmail());
            uzivatel.setSoukromyEmail(getSoukromyEmail());
            uzivatel.setTel(getTel());
            uzivatel.setPruznaPracDoba(getPruznaPracDoba());
            uzivatel.setDatumOd(getDatumOd());
            uzivatel.setDatumDo(getDatumDo());
            uzivatel.setPoznamka(getPoznamka());
            uzivatel.setPlatnostKeDni(getPlatnostKeDni());
            uzivatel.setCasAktualizace(getCasAktualizace());
            uzivatel.setUkonceno(getUkonceno());
            uzivatel.setAktivita(getAktivita());

            if (getZakazka() != null)
                uzivatel.setZakazka(new Zakazka(getZakazka().getId()));

            List<Opravneni> opravneniList = new ArrayList<>();
            if (getOpravneni() != null) {
                for (OpravneniDto opravneni : getOpravneni()) {
                    opravneniList.add(new Opravneni(opravneni.getId()));
                }
            }
            uzivatel.setOpravneni(opravneniList);

            List<Zavod> zavodList = new ArrayList<>();
            if (getOstatniZavody() != null) {
                for (ZavodDto zavod : getOstatniZavody()) {
                    zavodList.add(new Zavod(zavod.getId()));
                }
            }
            uzivatel.setOstatniZavody(zavodList);
        }

        return uzivatel;
    }
}
