package cz.diamo.share.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AktualizacePristupovychKaretResponseDto {

    private Integer pocetAktualizovanych = 0;

    private List<String> listSapIdZamestnanec = new ArrayList<String>();
}
