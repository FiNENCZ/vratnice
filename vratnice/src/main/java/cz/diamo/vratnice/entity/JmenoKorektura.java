package cz.diamo.vratnice.entity;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
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
@Table(name = "jmeno_korektura", schema = Constants.SCHEMA)
@NamedQuery(name = "JmenoKorektura.findAll", query = "SELECT s from JmenoKorektura s")
public class JmenoKorektura implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_jmeno_korektura")
    private String idJmenoKorektura;

    @Column(name = "jmeno_vstup")
    private String jmenoVstup;

    private String korektura;

    public JmenoKorektura(String idJmenoKorektura) {
        setIdJmenoKorektura(idJmenoKorektura);
    }
}
