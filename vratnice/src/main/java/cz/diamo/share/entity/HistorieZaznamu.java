package cz.diamo.share.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "historie_zaznamu", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieZaznamu.findAll", query = "SELECT s FROM HistorieZaznamu s")
public class HistorieZaznamu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "HISTORIE_ZAZNAMU_IDHISTORIE_ZAZNAMU_GENERATOR", sequenceName = Constants.SCHEMA + ".seq_historie_zaznamu_id_historie_zaznamu", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HISTORIE_ZAZNAMU_IDHISTORIE_ZAZNAMU_GENERATOR")
    @Column(name = "id_historie_zaznamu")
    private BigInteger idHistorieZaznamu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historie_zaznamu_typ")
    private HistorieZaznamuTyp historieZaznamuTyp;

    @Column(name = "id_zaznamu")
    private String idZaznamu;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "table_column")
    private String tableColumn;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    @Column(name = "zmenu_provedl_txt")
    private String zmenuProvedlTxt;

    @Column(name = "cas_zmn")
    private Date casZmn;

    @Transient
    private String popisSloupce;
}