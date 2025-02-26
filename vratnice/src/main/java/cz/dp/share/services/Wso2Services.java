package cz.dp.share.services;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import cz.dp.share.annotation.TransactionalRO;
import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.configuration.AppProperties;
import cz.dp.share.dto.Ws02EmailDto;
import cz.dp.share.dto.Ws02ZastupDto;
import cz.dp.share.dto.Wso2UzivatelExtDto;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.ValidationException;

@Service
@TransactionalROE
public class Wso2Services {

        final static Logger logger = LogManager.getLogger(Wso2Services.class);

        @Autowired
        private AppProperties appProperties;

        @Autowired
        private MessageSource messageSource;

        @Autowired
        private RestOperations restWso2;

        @TransactionalRO
        public String poslatEmail(Ws02EmailDto email) {

                try {
                        // kontrola URL
                        if (StringUtils.isBlank(appProperties.getWso2Url()))
                                throw new ValidationException(
                                                messageSource.getMessage("url.wso2.mail.null", null,
                                                                LocaleContextHolder.getLocale()));

                        if (StringUtils.isBlank(email.getTo()))
                                throw new ValidationException(
                                                messageSource.getMessage("wso2.mail.prijemce.require", null,
                                                                LocaleContextHolder.getLocale()));

                        // nahrazení enterů
                        if (!StringUtils.isBlank(email.getContent()))
                                email.setContent(email.getContent().replace("\n", "</br>"));

                        HttpEntity<Ws02EmailDto> requestEntity = new HttpEntity<Ws02EmailDto>(email,
                                        new HttpHeaders());

                        ResponseEntity<Void> result = restWso2
                                        .exchange("/email/send", HttpMethod.POST,
                                                        requestEntity, Void.class,
                                                        new HashMap<>());

                        if (result.getStatusCode().isError())
                                throw new BaseException(
                                                String.format(
                                                                messageSource.getMessage("wso2.email.send.error", null,
                                                                                LocaleContextHolder.getLocale()),
                                                                result.getStatusCode()));

                        return null;
                } catch (Exception e) {
                        return e.toString();
                }
        }

        @TransactionalRO
        public String zastupy(List<Ws02ZastupDto> zastupy) {

                try {
                        // kontrola URL
                        if (StringUtils.isBlank(appProperties.getWso2Url()))
                                throw new ValidationException(
                                                messageSource.getMessage("wso2.url.null", null,
                                                                LocaleContextHolder.getLocale()));
                        HttpEntity<Ws02ZastupDto[]> requestEntity = new HttpEntity<Ws02ZastupDto[]>(
                                        (Ws02ZastupDto[]) zastupy.toArray(new Ws02ZastupDto[0]),
                                        new HttpHeaders());

                        ResponseEntity<Void> result = restWso2
                                        .exchange("/zastupy", HttpMethod.POST,
                                                        requestEntity, Void.class,
                                                        new HashMap<>());

                        if (result.getStatusCode().isError())
                                throw new BaseException(
                                                String.format(
                                                                messageSource.getMessage("wso2.zastupy.error", null,
                                                                                LocaleContextHolder.getLocale()),
                                                                result.getStatusCode()));

                        return null;
                } catch (Exception e) {
                        return e.toString();
                }
        }

        @TransactionalRO
        public void uzivateleExterni(List<Wso2UzivatelExtDto> uzivatele) throws Exception {

                try {
                        // kontrola URL
                        if (StringUtils.isBlank(appProperties.getWso2Url()))
                                throw new ValidationException(
                                                messageSource.getMessage("wso2.url.null", null,
                                                                LocaleContextHolder.getLocale()));
                        HttpEntity<Wso2UzivatelExtDto[]> requestEntity = new HttpEntity<Wso2UzivatelExtDto[]>(
                                        (Wso2UzivatelExtDto[]) uzivatele.toArray(new Wso2UzivatelExtDto[0]),
                                        new HttpHeaders());

                        ResponseEntity<Void> result = restWso2
                                        .exchange("/externi-uzivatele", HttpMethod.POST,
                                                        requestEntity, Void.class,
                                                        new HashMap<>());

                        if (result.getStatusCode().isError())
                                throw new BaseException(
                                                String.format(
                                                                messageSource.getMessage("wso2.externi.uzivatele.error",
                                                                                null,
                                                                                LocaleContextHolder.getLocale()),
                                                                result.getStatusCode()));
                } catch (Exception e) {
                        throw new BaseException(
                                        String.format(
                                                        messageSource.getMessage("wso2.externi.uzivatele.error",
                                                                        null,
                                                                        LocaleContextHolder.getLocale()),
                                                        e.toString()));
                }
        }
}