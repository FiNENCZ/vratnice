package cz.diamo.share.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "zdrojovy_text", schema = Constants.SCHEMA)
@NamedQuery(name = "ZdrojovyText.findAll", query = "SELECT s FROM ZdrojovyText s")
public class ZdrojovyText implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ZDROJOVY_TEXT_IDZDROJOVYTEXT_GENERATOR", sequenceName = Constants.SCHEMA
            + ".seq_zdrojovy_text_id_zdrojovy_text", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZDROJOVY_TEXT_IDZDROJOVYTEXT_GENERATOR")
    @Column(name = "id_zdrojovy_text")
    private Integer idZdrojovyText;

    private String culture;

    private String hash;

    private String text;
}