package cz.dp.vratnice.edos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SnimacVratniceDto {

    private String id;

    private Integer cislo;

    private String lokalitaNazev;

    private String umisteni;
}
