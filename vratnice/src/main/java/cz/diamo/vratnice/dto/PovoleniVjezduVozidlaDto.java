package cz.diamo.vratnice.dto;

import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
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

    @Valid
    private SpolecnostDto spolecnostZadatele;

    private String icoZadatele;

    @Email(message = "{povoleni.vjezdu.vozidla.email.invalid}")
    @NotBlank(message = "{povoleni.vjezdu.vozidla.email.require}")
    private String emailZadatele;

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

    @Valid
    private SpolecnostDto spolecnostVozidla;

    @NotNull(message = "{povoleni.vjezdu.vozidla.datum_od.require}")
    private Date datumOd;

    @NotNull(message = "{povoleni.vjezdu.vozidla.datum_do.require}")
    private Date datumDo;

    @NotNull(message = "{povoleni.vjezdu.vozidla.zavod.require}")
    private ZavodDto zavod;

    @NotNull(message = "{povoleni.vjezdu.vozidla.lokalita.require}")
    @NotEmpty(message = "{povoleni.vjezdu.vozidla.lokalita.require}")
    private List<LokalitaDto> lokality;

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
        this.spolecnostZadatele = new SpolecnostDto(povoleniVjezduVozidla.getSpolecnostZadatele());
        this.icoZadatele = povoleniVjezduVozidla.getIcoZadatele();
        this.emailZadatele = povoleniVjezduVozidla.getEmailZadatele();
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
        this.spolecnostVozidla = new SpolecnostDto(povoleniVjezduVozidla.getSpolecnostVozidla());
        this.datumOd = povoleniVjezduVozidla.getDatumOd();
        this.datumDo = povoleniVjezduVozidla.getDatumDo();

        this.zavod = new ZavodDto(povoleniVjezduVozidla.getZavod());

        List<LokalitaDto> lokalitaDtos = new ArrayList<>();
        if (povoleniVjezduVozidla.getZavod() != null) {
            for (Lokalita lokalita : povoleniVjezduVozidla.getLokality()) {
                lokalitaDtos.add(new LokalitaDto(lokalita));
            }
        }
        this.setLokality(lokalitaDtos);

        

        this.opakovanyVjezd = povoleniVjezduVozidla.getOpakovanyVjezd();
        this.stav = povoleniVjezduVozidla.getStav();
    }

    public PovoleniVjezduVozidla toEntity() {
        PovoleniVjezduVozidla povoleniVjezduVozidla = new PovoleniVjezduVozidla();
    
        povoleniVjezduVozidla.setIdPovoleniVjezduVozidla(this.idPovoleniVjezduVozidla);
        povoleniVjezduVozidla.setJmenoZadatele(this.jmenoZadatele);
        povoleniVjezduVozidla.setPrijmeniZadatele(this.prijmeniZadatele);
        povoleniVjezduVozidla.setSpolecnostZadatele(getSpolecnostZadatele().toEntity());
        povoleniVjezduVozidla.setIcoZadatele(this.icoZadatele);
        povoleniVjezduVozidla.setEmailZadatele(this.emailZadatele);
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
        povoleniVjezduVozidla.setSpolecnostVozidla(getSpolecnostVozidla().toEntity());
        povoleniVjezduVozidla.setDatumOd(this.datumOd);
        povoleniVjezduVozidla.setDatumDo(this.datumDo);

        povoleniVjezduVozidla.setZavod(new Zavod(getZavod().getId()));
    
        List<Lokalita> lokality = new ArrayList<>();
        if (getZavod() != null) {
            for (LokalitaDto lokalitaDto : this.getLokality()) {
                lokality.add(new Lokalita(lokalitaDto.getId()));
            }
        }
        povoleniVjezduVozidla.setLokality(lokality);
        
    
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


    @AssertTrue(message = "{povoleni.vjezdu.vozidla.rz_vozidla.require}")
    private boolean isRzVozidlaElementCompleted() {
        if (rzVozidla == null) { 
            return true; // null není validace anotace, použití @NotNull to vyřeší
        }

        for (String element : rzVozidla) {
            if (element == null || element.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @AssertTrue(message = "{povoleni.vjezdu.vozidla.lokalita.invalid}")
    private boolean isLokalityHaveSameZavod() {
        if (lokality == null || zavod == null) {
            return true; // null není validace anotace, použití @NotNull to vyřeší
        }
        for (LokalitaDto lokalita : lokality) {
            if (!zavod.getId().equals(lokalita.getZavod().getId())) {
                return false;
            }
        }
        return true;
    }
}
