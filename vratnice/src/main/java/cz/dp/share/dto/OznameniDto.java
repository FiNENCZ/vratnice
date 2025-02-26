package cz.dp.share.dto;

import java.util.Date;

import cz.dp.share.enums.TypOznameniEnum;
import lombok.Data;

@Data
public class OznameniDto {
    private String id;
    private VytvorilDto vytvoril;
    private Date vytvoreno;
    private String nadpis;
    private String text;
    private String url;
    private Boolean precteno;
    private TypOznameniEnum typ;
}
