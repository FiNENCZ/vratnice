package cz.diamo.vratnice.dto;

import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class PovoleniVjezduVozidlaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idPovoleniVjezduVozidla;

    @NotBlank(message = "{povoleni.vjezdu.vozidla.jmeno_zadatele.require}")
    @Size(max = 30, message = "{povoleni.vjezdu.vozidla.jmeno_zadatele.max.30}")
    private String jmenoZadatele;

    @NotBlank(message = "{povoleni.vjezdu.vozidla.prijmeni_zadatele.require}")
    @Size(max = 30, message = "{povoleni.vjezdu.vozidla.prijmeni_zadatele.max.30}")
    private String prijmeniZadatele;

    @NotBlank(message = "{povoleni.vjezdu.vozidla.spolecnost_zadatele.require}")
    @Size(max = 120, message = "{povoleni.vjezdu.vozidla.spolecnost_zadatele.max.120}")
    private String spolecnostZadatele;

    private String icoZadatele;

    private String duvodZadosti;

    @NotNull(message = "{povoleni.vjezdu.vozidla.rz_vozidla.require}")
    private List<String> rzVozidla;

    @NotNull(message = "{povoleni.vjezdu.vozidla.typ_vozidla.require}")
    @NotEmpty(message = "{povoleni.vjezdu.vozidla.typ_vozidla.require}")
    private List<VozidloTypDto> typVozidla;

    @NotNull(message = "{povoleni.vjezdu.vozidla.zeme_registrace_vozidla.require}")
    private StatDto zemeRegistraceVozidla;

    @Valid
    private RidicDto ridic;

    private String spolecnostVozidla;

    @NotNull(message = "{povoleni.vjezdu.vozidla.datum_od.require}")
    private Date datumOd;

    @NotNull(message = "{povoleni.vjezdu.vozidla.datum_do.require}")
    private Date datumDo;

    @NotNull(message = "{povoleni.vjezdu.vozidla.zavod.require}")
    private List<ZavodDto> zavod;

    private Boolean opakovanyVjezd = false;

    private String stav = "vyžádáno";

    private Integer pocetVjezdu;

    public PovoleniVjezduVozidlaDto(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        if (povoleniVjezduVozidla == null) {
            return;
        }

        this.idPovoleniVjezduVozidla = povoleniVjezduVozidla.getIdPovoleniVjezduVozidla();
        this.jmenoZadatele = povoleniVjezduVozidla.getJmenoZadatele();
        this.prijmeniZadatele = povoleniVjezduVozidla.getPrijmeniZadatele();
        this.spolecnostZadatele = povoleniVjezduVozidla.getSpolecnostZadatele();
        this.icoZadatele = povoleniVjezduVozidla.getIcoZadatele();
        this.duvodZadosti = povoleniVjezduVozidla.getDuvodZadosti();

        List<String> rzVozidla = new ArrayList<>();
        if (povoleniVjezduVozidla.getRzVozidla() != null) {
            for (String rzVozidlo : povoleniVjezduVozidla.getRzVozidla()) {
                rzVozidla.add(new String(rzVozidlo));
            }
        }
        this.setRzVozidla(rzVozidla);

        List<VozidloTypDto> vozidloTypDtos = new ArrayList<>();
        if (povoleniVjezduVozidla.getTypVozidla() != null) {
            for(VozidloTyp vozidloTyp : povoleniVjezduVozidla.getTypVozidla()){
                vozidloTypDtos.add(new VozidloTypDto(vozidloTyp));
            }
        }
        this.setTypVozidla(vozidloTypDtos);
     
        this.zemeRegistraceVozidla = new StatDto(povoleniVjezduVozidla.getZemeRegistraceVozidla());
        this.ridic = new RidicDto(povoleniVjezduVozidla.getRidic());
        this.spolecnostVozidla = povoleniVjezduVozidla.getSpolecnostVozidla();
        this.datumOd = povoleniVjezduVozidla.getDatumOd();
        this.datumDo = povoleniVjezduVozidla.getDatumDo();

        List<ZavodDto> zavodDtos = new ArrayList<>();
        if (povoleniVjezduVozidla.getZavod() != null) {
            for (Zavod zavod : povoleniVjezduVozidla.getZavod()) {
                zavodDtos.add(new ZavodDto(zavod));
            }
        }
        this.setZavod(zavodDtos);

        this.opakovanyVjezd = povoleniVjezduVozidla.getOpakovanyVjezd();
        this.stav = povoleniVjezduVozidla.getStav();
    }

    public PovoleniVjezduVozidla toEntity() {
        PovoleniVjezduVozidla povoleniVjezduVozidla = new PovoleniVjezduVozidla();
    
        povoleniVjezduVozidla.setIdPovoleniVjezduVozidla(this.idPovoleniVjezduVozidla);
        povoleniVjezduVozidla.setJmenoZadatele(this.jmenoZadatele);
        povoleniVjezduVozidla.setPrijmeniZadatele(this.prijmeniZadatele);
        povoleniVjezduVozidla.setSpolecnostZadatele(this.spolecnostZadatele);
        povoleniVjezduVozidla.setIcoZadatele(this.icoZadatele);
        povoleniVjezduVozidla.setDuvodZadosti(this.duvodZadosti);
    
        List<String> rzVozidla = new ArrayList<>();
        if (this.getRzVozidla() != null) {
            for (String rzVozidlo : this.getRzVozidla()) {
                rzVozidla.add(new String(rzVozidlo));
            }
        }
        povoleniVjezduVozidla.setRzVozidla(rzVozidla);

        List<VozidloTyp> vozidlaTypy = new ArrayList<>();
        if (getTypVozidla() != null) {
            for (VozidloTypDto vozidloTypDto : this.getTypVozidla()) {
                vozidlaTypy.add(new VozidloTyp(vozidloTypDto.getTypEnum()));
            }
        }
        povoleniVjezduVozidla.setTypVozidla(vozidlaTypy);
    

        povoleniVjezduVozidla.setZemeRegistraceVozidla(getZemeRegistraceVozidla().toEntity());
        povoleniVjezduVozidla.setRidic(this.ridic != null ? this.ridic.toEntity() : null); // Pokud je ridic null, nastavíme null
        povoleniVjezduVozidla.setSpolecnostVozidla(this.spolecnostVozidla);
        povoleniVjezduVozidla.setDatumOd(this.datumOd);
        povoleniVjezduVozidla.setDatumDo(this.datumDo);
    
        List<Zavod> zavody = new ArrayList<>();
        if (getZavod() != null) {
            for (ZavodDto zavodDto : this.getZavod()) {
                zavody.add(new Zavod(zavodDto.getId()));
            }
        }
        povoleniVjezduVozidla.setZavod(zavody);
    
        povoleniVjezduVozidla.setOpakovanyVjezd(this.opakovanyVjezd);
        povoleniVjezduVozidla.setStav(this.stav);
    
        return povoleniVjezduVozidla;
    }

    @AssertTrue(message = "{povoleni.vjezdu.vozidla.rz_typ_vozidla.require}")
    private boolean isRzVozidlaTypVozidlaCountEqual() {
        if (rzVozidla == null || typVozidla == null) {
            return false;
        }
        return rzVozidla.size() == typVozidla.size();
    }
    
    @AssertTrue(message = "{povoleni.vjezdu.vozidla.datum_od_datum_do}")
    private boolean isDatumOdBeforeDatumDo() {
        if (datumOd == null || datumDo == null) {
            return true; // pokud jsou data null, nechceme aby validace selhala zde
        }
        return !datumDo.before(datumOd);
    }

    @AssertTrue(message = "{povoleni.vjezdu.vozidla.rz_vozidla.unique}")
    private boolean isRzVozidlaUnique() {
        if (rzVozidla == null) {
            return true; // pokud je seznam null, nechceme aby validace selhala zde
        }
        Set<String> uniqueRzVozidla = new HashSet<>(rzVozidla);
        return uniqueRzVozidla.size() == rzVozidla.size();
    }
}
