package cz.diamo.share.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ZastupSimple implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Column(name = "id_zavod")
    private String idZavod;

    @Column(name = "sap_id")
    private String sapid;

    private String nazev;

}