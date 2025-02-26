package cz.dp.share.services;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.dp.share.entity.ZdrojovyText;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.repository.ZdrojovyTextRepository;

@Service
@Transactional(readOnly = true)
public class ZdrojovyTextServices {

    @Autowired
    private ZdrojovyTextRepository zdrojovyTextRepository;

    @Autowired
    MessageSource messageSource;

    public String getResources(Locale locale, String hash) throws RecordNotFoundException {
        ZdrojovyText zdrojovyText = zdrojovyTextRepository.getDetail(locale.getLanguage(), hash);

        if (zdrojovyText != null)
            return zdrojovyText.getText();
        else
            throw new RecordNotFoundException(
                    String.format(messageSource.getMessage("resources.not.found", null, locale), hash));

    }

    public List<ZdrojovyText> getAllRersources() {
        return zdrojovyTextRepository.findAll();
    }

}
