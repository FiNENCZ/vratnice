package cz.dp.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pracovni_pozice_log", schema = Constants.SCHEMA)
@NamedQuery(name = "PracovniPoziceLog.findAll", query = "SELECT s FROM PracovniPoziceLog s")
public class PracovniPoziceLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_pracovni_pozice_log")
    private String idPracovniPoziceLog;

    @Column(name = "cas_volani")
    private Date casVolani;

    @Column(name = "cas_zpracovani")
    private Date casZpracovani;

    private String chyba;

    @Column(name = "pocet_zaznamu")
    private Integer pocetZaznamu;

    private boolean ok;

    @Column(name = "jsonLog")
    private String jsonLog;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;
}