package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.NavstevniListekTypEnum;
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
@Table(name = "navstevni_listek_typ", schema = Constants.SCHEMA)
@NamedQuery(name = "NavstevniListekTyp.findAll", query = "SELECT s FROM NavstevniListekTyp s")
public class NavstevniListekTyp implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_navstevni_listek_typ")
    private Integer idNavstevniListekTyp;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public NavstevniListekTyp(NavstevniListekTypEnum value) {
        setIdNavstevniListekTyp(value.getValue());
    }

    public NavstevniListekTypEnum getNavstevniListekTypEnum() {
        return NavstevniListekTypEnum.getNavstevniListekTypEnum(getIdNavstevniListekTyp());
    }

    

}
