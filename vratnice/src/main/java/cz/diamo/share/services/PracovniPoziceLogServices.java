package cz.diamo.share.services;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.PracovniPoziceLog;
import cz.diamo.share.repository.PracovniPoziceLogRepository;
import cz.diamo.share.rest.dto.PracovniPoziceDto;

@Service
@TransactionalROE
public class PracovniPoziceLogServices {

    final static Logger logger = LogManager.getLogger(PracovniPoziceLogServices.class);

    @Autowired
    private PracovniPoziceLogRepository pracovniPoziceLogRepository;

    public PracovniPoziceLog getLastOk() {
        return pracovniPoziceLogRepository.getLastOk();
    }

    public List<PracovniPoziceLog> getList() {
        return pracovniPoziceLogRepository.getList(null);
    }

    @TransactionalWrite
    public PracovniPoziceLog save(PracovniPoziceLog pracovniPoziceLog, List<PracovniPoziceDto> zaznamy,
            String chyba, boolean logJson) {
        try {
            if (pracovniPoziceLog == null)
                pracovniPoziceLog = new PracovniPoziceLog();
            boolean zalozeni = StringUtils.isBlank(pracovniPoziceLog.getIdPracovniPoziceLog());
            pracovniPoziceLog.setAktivita(true);

            Calendar calendarNow = Calendar.getInstance();

            pracovniPoziceLog.setPocetZaznamu(Integer.valueOf(0));
            if (zaznamy != null)
                pracovniPoziceLog.setPocetZaznamu(zaznamy.size());

            if (!StringUtils.isBlank(chyba)) {
                pracovniPoziceLog.setOk(false);
                pracovniPoziceLog.setChyba(chyba);
                if (logJson && zaznamy != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setSerializationInclusion(Include.NON_NULL);
                    try {
                        String jsonLog = mapper.writeValueAsString(zaznamy);
                        pracovniPoziceLog.setJsonLog(jsonLog);
                    } catch (JsonProcessingException e) {
                        logger.error(e);
                    }
                }

            } else
                pracovniPoziceLog.setOk(true);

            if (zalozeni) {
                pracovniPoziceLog.setCasVolani(calendarNow.getTime());
                pracovniPoziceLog.setOk(false);
                if (StringUtils.isBlank(pracovniPoziceLog.getChyba()))
                    pracovniPoziceLog.setChyba("Probíhá zpracování dat ...");
            } else
                pracovniPoziceLog.setCasZpracovani(calendarNow.getTime());

            if (pracovniPoziceLog.isOk()
                    && pracovniPoziceLog.getPocetZaznamu().compareTo(Integer.valueOf(0)) != 1) {
                pracovniPoziceLog.setOk(false);
                pracovniPoziceLog.setChyba("Žádný záznam organizační struktury");
            }

            if (pracovniPoziceLog.isOk())
                pracovniPoziceLog.setChyba(null);

            pracovniPoziceLog.setZmenuProvedl(Utils.getZmenuProv());
            pracovniPoziceLog.setCasZmn(new Timestamp(calendarNow.getTimeInMillis()));
            pracovniPoziceLog = pracovniPoziceLogRepository.save(pracovniPoziceLog);
            return pracovniPoziceLog;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }

    }

}
