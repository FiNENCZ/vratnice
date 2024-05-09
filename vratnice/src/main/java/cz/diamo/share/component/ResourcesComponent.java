package cz.diamo.share.component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import cz.diamo.share.entity.ZdrojovyText;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.ZdrojovyTextServices;

@Component
public class ResourcesComponent {

    final static Logger logger = LogManager.getLogger(ResourcesComponent.class);

    @Autowired
    private ZdrojovyTextServices zdrojovyTextServices;

    @Autowired
    MessageSource messageSource;

    private LinkedHashMap<String, String> hashMap;

    public String getResources(Locale locale, String hash) throws RecordNotFoundException, NoSuchMessageException {

        return getResources(locale, hash, true);
    }

    public String getResources(Locale locale, String hash, boolean chybaPokudNexistuje)
            throws RecordNotFoundException, NoSuchMessageException {

        // prvotní načtení všech resources
        if (hashMap == null || hashMap.size() < 1)
            reload();

        String value = hashMap.get(locale.getLanguage() + "#" + hash);
        if (!StringUtils.isBlank(value))
            return value;
        else if (chybaPokudNexistuje)
            throw new RecordNotFoundException(
                    String.format(messageSource.getMessage("resources.not.found", null, locale), hash));
        else
            return hash;
    }

    public void reload() {
        hashMap = new LinkedHashMap<String, String>();
        List<ZdrojovyText> resources = zdrojovyTextServices.getAllRersources();
        if (resources != null && resources.size() > 0) {
            for (ZdrojovyText zdrojovyText : resources) {
                hashMap.put(zdrojovyText.getCulture() + "#" + zdrojovyText.getHash(), zdrojovyText.getText());
            }
        }
    }
}
