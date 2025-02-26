package cz.dp.share.services;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.entity.TmpStavOperace;
import cz.dp.share.enums.StavOperaceIdEnum;
import cz.dp.share.enums.StavOperaceStatusEnum;
import cz.dp.share.repository.TmpStavOperaceRepository;

@Service
@TransactionalROE
public class StavOperaceServices {

    final static Logger logger = LogManager.getLogger(StavOperaceServices.class);

    @Autowired
    private TmpStavOperaceRepository tmpStavOperaceRepository;

    // @Autowired
    // private WebSocketServices webSocketServices;

    public TmpStavOperace getDetail(String idUzivatel, StavOperaceIdEnum idOperace) {
        TmpStavOperace tmpStavOperace = tmpStavOperaceRepository.getDetail(idUzivatel, idOperace.toString());
        return tmpStavOperace;
    }

    public static TmpStavOperace getTmpStavOperace(String idUzivatel, StavOperaceIdEnum idOperace,
            StavOperaceStatusEnum status, String title, String message, Integer size, Integer actual) {
        TmpStavOperace tmpStavOperace = new TmpStavOperace();
        tmpStavOperace.setIdUzivatel(idUzivatel);
        tmpStavOperace.setIdOperaceEnum(idOperace);
        Date now = Calendar.getInstance().getTime();
        tmpStavOperace.setCas(now);
        tmpStavOperace.setStart(now);
        tmpStavOperace.setTitle(title);
        tmpStavOperace.setMessage(message);
        tmpStavOperace.setStatusEnum(status);
        tmpStavOperace.setSize(size);
        tmpStavOperace.setActual(actual);
        return tmpStavOperace;
    }

    public static TmpStavOperace getTmpStavOperace(String idUzivatel, StavOperaceIdEnum idOperace, String title,
            String message) {
        TmpStavOperace tmpStavOperace = new TmpStavOperace();
        tmpStavOperace.setIdUzivatel(idUzivatel);
        tmpStavOperace.setIdOperaceEnum(idOperace);
        Date now = Calendar.getInstance().getTime();
        tmpStavOperace.setCas(now);
        tmpStavOperace.setStart(now);
        tmpStavOperace.setTitle(title);
        tmpStavOperace.setMessage(message);
        tmpStavOperace.setStatusEnum(StavOperaceStatusEnum.ERROR);
        return tmpStavOperace;
    }

    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void saveAsync(TmpStavOperace maintstavoperace, boolean sendWebSocket) {
        save(maintstavoperace, sendWebSocket);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void save(TmpStavOperace tmpStavOperace, boolean sendWebSocket) {
        TmpStavOperace tmpstavOperacePuv = tmpStavOperaceRepository.getDetail(tmpStavOperace.getIdUzivatel(),
                tmpStavOperace.getIdOperace());
        if (tmpstavOperacePuv != null) {
            tmpstavOperacePuv.setTmpStavOperace(tmpStavOperace);
            tmpStavOperaceRepository.save(tmpstavOperacePuv);

        } else
            tmpStavOperaceRepository.save(tmpStavOperace);

        if (sendWebSocket) {
            // WebSocketMessageDto webSocketMessageDto = new
            // WebSocketMessageDto(tmpStavOperace);
            // webSocketServices.sendAsync(tmpStavOperace.getIdUzivatel(),
            // webSocketMessageDto);
        }
    }

    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteAsync(String idUzivatel, StavOperaceIdEnum idOperace) {
        tmpStavOperaceRepository.deleteByPk(idUzivatel, idOperace.toString());
    }

}