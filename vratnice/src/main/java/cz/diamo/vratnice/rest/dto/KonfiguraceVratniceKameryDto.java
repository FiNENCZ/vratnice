package cz.diamo.vratnice.rest.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KonfiguraceVratniceKameryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String idZavod;

    private String idLokalita;

    private String vratniceApiUrl;

    private Integer casOdeslaniNevyporadanychZaznamuVSekundach;

    private Integer casInicializaceVSekundach;

    private String poznamka;

    private Timestamp casZmn;

    private String zmenuProvedl;

}
