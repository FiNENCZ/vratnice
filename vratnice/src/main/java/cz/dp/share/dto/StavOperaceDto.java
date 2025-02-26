package cz.dp.share.dto;

import java.util.Date;

import cz.dp.share.entity.TmpStavOperace;
import cz.dp.share.enums.StavOperaceIdEnum;
import cz.dp.share.enums.StavOperaceStatusEnum;
import lombok.Data;

@Data
public class StavOperaceDto {

    private StavOperaceIdEnum id;
    private StavOperaceStatusEnum status;
    private String title;
    private String message;
    private Integer progress;
    private Date time;
    private Integer trvani;

    public StavOperaceDto(TmpStavOperace tmpStavOperace) {

        setId(tmpStavOperace.getIdOperaceEnum());
        setStatus(tmpStavOperace.getStatusEnum());
        setTitle(tmpStavOperace.getTitle());
        setMessage(tmpStavOperace.getMessage());
        setProgress(tmpStavOperace.getProgress());
        setTime(tmpStavOperace.getCas());
        setTrvani(tmpStavOperace.getTrvani());
    }
}
