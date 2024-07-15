package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.vratnice.entity.NajemnikNavstevnickaKarta;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NajemnikNavstevnickaKartaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idNajemnikNavstevnickaKarta;

    @NotBlank(message = "{ridic.jmeno.require}")
    @Size(max = 50, message = "{ridic.jmeno.max.50}")
    private String jmeno;

    @NotBlank(message = "{ridic.prijmeni.require}")
    @Size(max = 50, message = "{ridic.prijmeni.max.50}")
    private String prijmeni;

    @NotBlank(message = "{ridic.cisloOp.require}")
    @Size(max = 30, message = "{ridic.cisloOp.max.30}")
    private String cisloOp;

    @Size(max = 120, message = "{ridic.firma.max.120}")
    private String spolecnost;

    @Size(max = 30, message = "{ridic.cisloOp.max.30}")
    private String cisloNajemniSmlouvy;

    @NotBlank(message = "{ridic.cisloOp.require}")
    @Size(max = 30, message = "{ridic.cisloOp.max.30}")
    private String cisloKarty;

    private String duvodVydani;

    private Date vydanoOd;

    private Date vydanoDo;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public NajemnikNavstevnickaKartaDto(NajemnikNavstevnickaKarta najemnikNavstevnickaKarta) {
        if(najemnikNavstevnickaKarta == null) {
            return;
        }

        this.idNajemnikNavstevnickaKarta = najemnikNavstevnickaKarta.getIdNajemnikNavstevnickaKarta();
        this.jmeno = najemnikNavstevnickaKarta.getJmeno();
        this.prijmeni = najemnikNavstevnickaKarta.getPrijmeni();
        this.cisloOp = najemnikNavstevnickaKarta.getCisloOp();
        this.spolecnost = najemnikNavstevnickaKarta.getSpolecnost();
        this.cisloNajemniSmlouvy = najemnikNavstevnickaKarta.getCisloNajemniSmlouvy();
        this.cisloKarty = najemnikNavstevnickaKarta.getCisloKarty();
        this.duvodVydani = najemnikNavstevnickaKarta.getDuvodVydani();
        this.vydanoOd = najemnikNavstevnickaKarta.getVydanoOd();
        this.vydanoDo = najemnikNavstevnickaKarta.getVydanoDo();
        this.aktivita = najemnikNavstevnickaKarta.getAktivita();
    }

    public NajemnikNavstevnickaKarta toEntity() {
        NajemnikNavstevnickaKarta najemnikNavstevnickaKarta = new NajemnikNavstevnickaKarta();

        najemnikNavstevnickaKarta.setIdNajemnikNavstevnickaKarta(this.idNajemnikNavstevnickaKarta);
        najemnikNavstevnickaKarta.setJmeno(this.jmeno);
        najemnikNavstevnickaKarta.setPrijmeni(this.prijmeni);
        najemnikNavstevnickaKarta.setCisloOp(this.cisloOp);
        najemnikNavstevnickaKarta.setSpolecnost(this.spolecnost);
        najemnikNavstevnickaKarta.setCisloNajemniSmlouvy(this.cisloNajemniSmlouvy);
        najemnikNavstevnickaKarta.setCisloKarty(this.cisloKarty);
        najemnikNavstevnickaKarta.setDuvodVydani(this.duvodVydani);
        najemnikNavstevnickaKarta.setVydanoOd(this.vydanoOd);
        najemnikNavstevnickaKarta.setVydanoDo(this.vydanoDo);
        najemnikNavstevnickaKarta.setAktivita(this.aktivita);

        return najemnikNavstevnickaKarta;
    }

    @AssertTrue(message = "{najemnik_navstevnicka_karta.vydano_od_vydano_do}")
    private boolean isDatumOdBeforeDatumDo() {
        if (vydanoOd == null || vydanoDo == null) {
            return true; // pokud jsou data null, nechceme aby validace selhala zde
        }
        return !vydanoDo.before(vydanoOd);
    }
}
