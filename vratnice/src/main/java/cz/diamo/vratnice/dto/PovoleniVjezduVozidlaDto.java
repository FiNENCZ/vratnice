package cz.diamo.vratnice.dto;

import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class PovoleniVjezduVozidlaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idPovoleniVjezduVozidla;

    @NotBlank(message = "Jméno žadatele je povinná položka")
    @Size(max = 30, message = "Jméno žadatele nemůže přesáhnout 30 znaků")
    private String jmenoZadatele;

    @NotBlank(message = "Příjmení žadatele je povinná položka")
    @Size(max = 30, message = "Příjmení žadatele nemůže přesáhnout 30 znaků")
    private String prijmeniZadatele;

    @NotBlank(message = "Společnost žadatele je povinná položka")
    @Size(max = 120, message = "Společnost žadatele nemůže přesáhnout 120 znaků")
    private String spolecnostZadatele;

    private String icoZadatele;

    private String duvodZadosti;

    @NotNull(message = "Musí být vyplněna alespoň jedna registrační značka vozidla")
    private List<String> rzVozidla;

    @NotNull(message = "Musí být vyplněn alespoň jeden typ vozidla")
    private List<String> typVozidla;

    @NotBlank(message = "Země registrace vozidla je povinná položka")
    private String zemeRegistraceVozidla;

    private RidicDto ridic;

    private String spolecnostVozidla;

    @NotNull(message = "Datum od je povinná položka")
    private Date datumOd;

    @NotNull(message = "Datum do je povinná položka")
    private Date datumDo;

    @NotNull(message = "Musí být vybrána alespoň jedna lokalita (závod)")
    private List<ZavodDto> zavod;

    private Boolean opakovanyVjezd;

    private String stav = "vyžádáno";

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

        List<String> typVozidla = new ArrayList<>();
        if (povoleniVjezduVozidla.getTypVozidla() != null) {
            for (String vozidloTyp : povoleniVjezduVozidla.getTypVozidla()) {
                typVozidla.add(new String(vozidloTyp));
            }
        }
        this.setTypVozidla(typVozidla);

     
        this.zemeRegistraceVozidla = povoleniVjezduVozidla.getZemeRegistraceVozidla();
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
    
        List<String> typVozidla = new ArrayList<>();
        if (this.getTypVozidla() != null) {
            for (String vozidloTyp : this.getTypVozidla()) {
                typVozidla.add(new String(vozidloTyp));
            }
        }
        povoleniVjezduVozidla.setTypVozidla(typVozidla);
    
        povoleniVjezduVozidla.setZemeRegistraceVozidla(this.zemeRegistraceVozidla);
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
    
}
