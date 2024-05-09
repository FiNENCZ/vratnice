package cz.diamo.share.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tmp_opravneni_vyber", schema = Constants.SCHEMA)
@NamedQuery(name = "TmpOpravneniVyber.findAll", query = "SELECT s FROM TmpOpravneniVyber s")
public class TmpOpravneniVyber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_tmp_opravneni_vyber")
    private String idTmpOpravneniVyber;

    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Column(name = "id_uzivatel_podrizeny")
    private String idUzivatelPodrizeny;

    private String authority;
}