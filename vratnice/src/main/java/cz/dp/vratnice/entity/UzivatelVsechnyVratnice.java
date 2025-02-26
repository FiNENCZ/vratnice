package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
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
@Table(name = "uzivatel_vsechny_vratnice", schema = Constants.SCHEMA)
@NamedQuery(name = "UzivatelVsechnyVratnice.findAll", query = "SELECT s FROM UzivatelVsechnyVratnice s")
public class UzivatelVsechnyVratnice  implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Column(name = "aktivni_vsechny_vratnice")
    private Boolean aktivniVsechnyVratnice = false;

    public UzivatelVsechnyVratnice(Uzivatel uzivatel, Boolean aktivniVsechnyVratnice) {
        setIdUzivatel(uzivatel.getIdUzivatel());
        setAktivniVsechnyVratnice(aktivniVsechnyVratnice);
    }

}
