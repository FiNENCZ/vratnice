package cz.dp.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.dto.opravneni.OpravneniDto;
import cz.dp.share.dto.opravneni.RoleDto;
import cz.dp.share.entity.Opravneni;
import cz.dp.share.entity.Role;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.Zavod;
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

    @NotNull(message = "{datum.od.require}")
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

    private Boolean externi;

    private Boolean canEdit = false;

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
        setExterni(uzivatel.getExterni());
        setAktivita(uzivatel.getAktivita());

        if (uzivatel.getZakazka() != null)
            setZakazka(new ZakazkaDto(uzivatel.getZakazka()));

        List<OpravneniDto> opravneniDtos = new ArrayList<>();
        if (uzivatel.getOpravneni() != null) {
            for (Opravneni opravneni : uzivatel.getOpravneni()) {
                opravneniDtos.add(new OpravneniDto(opravneni, null, true, false));
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
        return getUzivatel(uzivatel, null, pouzeId);
    }

    @JsonIgnore
    public Uzivatel getUzivatel(Uzivatel uzivatel, AppUserDto appUserDto, boolean pouzeId) {
        boolean novy = false;
        if (uzivatel == null) {
            uzivatel = new Uzivatel();
            novy = true;
        }

        uzivatel.setIdUzivatel(getId());
        if (!pouzeId) {

            uzivatel.setAktivita(getAktivita());

            if (novy)
                uzivatel.setExterni(getExterni());

            if (uzivatel.getExterni()) {
                uzivatel.setZavod(new Zavod(appUserDto.getZavod().getId()));
                uzivatel.setNazev(getNazev());
                uzivatel.setJmeno(getJmeno());
                uzivatel.setPrijmeni(getPrijmeni());
                uzivatel.setEmail(getEmail());
                uzivatel.setTel(getTel());
                uzivatel.setDatumOd(getDatumOd());
                uzivatel.setDatumDo(getDatumDo());
                uzivatel.setPoznamka(getPoznamka());
                uzivatel.setCip1(getRfid1());
                uzivatel.setCip2(getRfid2());
                if (novy)
                    uzivatel.setPruznaPracDoba(true);
            }

            // if (getZakazka() != null)
            // uzivatel.setZakazka(new Zakazka(getZakazka().getId()));

            // List<Opravneni> opravneniList = new ArrayList<>();
            // if (getOpravneni() != null) {
            // for (OpravneniDto opravneni : getOpravneni()) {
            // opravneniList.add(new Opravneni(opravneni.getId()));
            // }
            // }
            // uzivatel.setOpravneni(opravneniList);

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
