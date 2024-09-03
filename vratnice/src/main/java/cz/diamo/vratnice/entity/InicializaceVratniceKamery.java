package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "inicializace_vratnice_kamery")
@NamedQuery(name = "InicializaceVratniceKamery.findAll", query = "SELECT s from InicializaceVratniceKamery s")
public class InicializaceVratniceKamery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_inicializace_vratnice_kamery")
    private String idInicializaceVratniceKamery;

    @Column(name = "ip_adresa", unique = true)
    private String ipAdresa;

    @Column(name = "cas_inicializace")
    private Timestamp casInicializace;

}
