package cz.diamo.share.dto;

import java.util.Date;

import cz.diamo.share.enums.TypOznameniEnum;
import lombok.Data;

@Data
public class OznameniDto {
    private String id;
    private UzivatelDto vytvoril;
    private Date vytvoreno;
    private String nadpis;
    private String text;
    private String url;
    private Boolean precteno;
    private TypOznameniEnum typ;
}
