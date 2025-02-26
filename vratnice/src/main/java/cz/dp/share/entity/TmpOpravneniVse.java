package cz.dp.share.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
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
@Table(name = "tmp_opravneni_vse", schema = Constants.SCHEMA)
@NamedQuery(name = "TmpOpravneniVse.findAll", query = "SELECT s FROM TmpOpravneniVse s")
public class TmpOpravneniVse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_tmp_opravneni_vse")
    private String idTmpOpravneniVse;

    @Column(name = "id_uzivatel")
    private String idUzivatel;

    private String authority;
}