package cz.diamo.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pracovni_pozice_podrizene", schema = Constants.SCHEMA)
@NamedQuery(name = "PracovniPozicePodrizene.findAll", query = "SELECT s FROM PracovniPozicePodrizene s")
public class PracovniPozicePodrizene implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_pracovni_pozice_podrizene")
    private String idPracovniPozicePodrizene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracovni_pozice")
    private PracovniPozice pracovniPozice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracovni_pozice_podrizeny")
    private PracovniPozice pracovniPozicePodrizeny;

    @Column(name = "primy_podrizeny")
    private Boolean primyPodrizeny = false;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;
}