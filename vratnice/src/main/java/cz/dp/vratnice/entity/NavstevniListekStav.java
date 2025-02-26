package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.NavstevniListekStavEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "navstevni_listek_stav", schema = Constants.SCHEMA)
@NamedQuery(name = "NavstevniListekStav.findAll", query = "SELECT s FROM NavstevniListekTyp s")
public class NavstevniListekStav implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_navstevni_listek_stav")
    private Integer idNavstevniListekStav;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public NavstevniListekStav(NavstevniListekStavEnum value) {
        setIdNavstevniListekStav(value.getValue());
    }

    public NavstevniListekStavEnum getNavstevniListekStavEnum() {
        return NavstevniListekStavEnum.getNavstevniListekStavEnum(getIdNavstevniListekStav());
    }


}
