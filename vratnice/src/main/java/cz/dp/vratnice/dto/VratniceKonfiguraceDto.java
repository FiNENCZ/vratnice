package cz.dp.vratnice.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VratniceKonfiguraceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String zavodNazev;

    private Integer dobaPouceni;

    private String barvaVyprseniPouceni;

    private String barvaNepovoleneSpz;

    private String barvaPovoleneSpz;

    private String barvaSluzebnihoVozidla;

}
