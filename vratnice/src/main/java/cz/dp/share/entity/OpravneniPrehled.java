package cz.dp.share.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class OpravneniPrehled implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "id_opravneni")
    private String idOpravneni;

    private String kod;

    private String nazev;

    @Column(name = "typ_pristupu")
    private String typPristupu;

    @Column(name = "zavod_id")
    private String zavodId;

    @Column(name = "zavod_sap_id")
    private String zavodSapId;

    @Column(name = "zavod_nazev")
    private String zavodNazev;

    @Column(name = "zamestnanec_id")
    private String zamestnanecId;

    @Column(name = "zamestnanec_sap_id")
    private String zamestnanecSapId;

    @Column(name = "zamestnanec_prijmeni")
    private String zamestnanecPrijmeni;

    @Column(name = "zamestnanec_jmeno")
    private String zamestnanecJmeno;

    @Column(name = "zamestnanec_nazev")
    private String zamestnanecNazev;

    private Boolean aktivita;
}