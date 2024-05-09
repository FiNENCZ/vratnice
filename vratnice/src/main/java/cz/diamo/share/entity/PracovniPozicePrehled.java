package cz.diamo.share.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class PracovniPozicePrehled implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "id_pracovni_pozice")
    private String idPracovniPozice;

    @Column(name = "sap_id")
    private String sapId;

    @Column(name = "sap_id_nadrizeny")
    private String sapIdNadrizeny;

    private String nazev;

    private String zkratka;

    @Column(name = "uzivatel_sap_id")
    private String uzivatelSapId;

    @Column(name = "uzivatel_nazev")
    private String uzivatelNazev;

    private String prijmeni;

    private String jmeno;

    private String email;

    private String tel;

    @Column(name = "zavod_nazev")
    private String zavodNazev;

    @Column(name = "zavod_sap_id")
    private String zavodSapId;

    public PracovniPozice getPracovniPozice() {
        PracovniPozice pracovniPozice = new PracovniPozice();
        pracovniPozice.setIdPracovniPozice(getIdPracovniPozice());
        return pracovniPozice;
    }

    public PracovniPozicePrehled(PracovniPozice pracovniPozice) {
        setIdPracovniPozice(pracovniPozice.getIdPracovniPozice());
    }
}