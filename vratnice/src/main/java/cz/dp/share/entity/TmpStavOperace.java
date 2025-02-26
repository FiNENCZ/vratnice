package cz.dp.share.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import cz.dp.share.base.Utils;
import cz.dp.share.constants.Constants;
import cz.dp.share.enums.StavOperaceIdEnum;
import cz.dp.share.enums.StavOperaceStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tmpStavOperace", schema = Constants.SCHEMA)
@IdClass(TmpStavOperace.class)
@NamedQuery(name = "TmpStavOperace.findAll", query = "SELECT t FROM TmpStavOperace t")
public class TmpStavOperace implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Id
    @Column(name = "id_operace")
    private String idOperace;

    private String title;

    private String message;

    private String status;

    private Integer size;

    private Integer actual;

    private Integer progress;

    private Date start;

    private Date cas;

    private Integer trvani;

    public void setTmpStavOperace(TmpStavOperace tmpStavOperace) {
        setTitle(tmpStavOperace.getTitle());
        setMessage(tmpStavOperace.getMessage());
        setStatus(tmpStavOperace.getStatus());
        setSize(tmpStavOperace.getSize());
        setActual(tmpStavOperace.getActual());
        setProgress(tmpStavOperace.getProgress());
        setStart(tmpStavOperace.getStart());
        setCas(tmpStavOperace.getCas());
        setTrvani(tmpStavOperace.getTrvani());
    }

    public StavOperaceStatusEnum getStatusEnum() {
        return StavOperaceStatusEnum.valueOf(getStatus());
    }

    public void setStatusEnum(StavOperaceStatusEnum stavOperaceStatusEnum) {
        setStatus(stavOperaceStatusEnum.toString());
    }

    public StavOperaceIdEnum getIdOperaceEnum() {
        return StavOperaceIdEnum.valueOf(getIdOperace());
    }

    public void setIdOperaceEnum(StavOperaceIdEnum stavOperaceIdEnum) {
        setIdOperace(stavOperaceIdEnum.toString());
    }

    private void changeTime() {
        setCas(Calendar.getInstance().getTime());
        setTrvani(Utils.rozdilVSekundach(getStart(), getCas()));
    }

    public void changeTitle(String title) {
        setTitle(title);
        changeTime();
    }

    public void changeMessage(String message) {
        setMessage(message);
        changeTime();
    }

    public void changeStatus(StavOperaceStatusEnum status) {
        setStatusEnum(status);
        changeTime();
    }

    public void changeStatus(StavOperaceStatusEnum status, String message) {
        setMessage(message);
        changeStatus(status);
    }

    public void changeActual(Integer actual) {
        setActual(actual);
        if (getSize() == null || getActual() == null)
            setProgress(null);
        else {
            if (getActual() == 0)
                setProgress(0);
            else
                setProgress(BigDecimal.valueOf(100).divide(BigDecimal.valueOf(getSize()), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(getActual())).setScale(0, RoundingMode.HALF_UP)
                        .intValueExact());
        }
    }

    public void addActual() {
        if (getActual() != null) {
            changeActual(getActual() + 1);
            changeTime();
        }

    }

    public void changeStatus(StavOperaceStatusEnum status, String message, Integer actual) {
        changeActual(actual);
        changeStatus(status, message);
    }

}