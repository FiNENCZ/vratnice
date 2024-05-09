package cz.diamo.share.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.entity.ExterniRole;
import cz.diamo.share.entity.ExterniUzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.repository.ExterniUzivatelRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalRO
public class ExterniUzivatelServices {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ExterniUzivatelRepository externiUzivatelRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public Boolean exists(String username) {
        return Utils.exists(externiUzivatelRepository.existsByUsername(username, ""));
    }

    public ExterniUzivatel getDetailByUserName(String username)
            throws RecordNotFoundException, NoSuchMessageException {
        return getDetail(externiUzivatelRepository.getDetailByUsername(username));
    }

    public ExterniUzivatel getDetail(String idExterniUzivatel)
            throws RecordNotFoundException, NoSuchMessageException {
        return getDetail(externiUzivatelRepository.getDetail(idExterniUzivatel));
    }

    public ExterniUzivatel getDetail(String username, String password)
            throws RecordNotFoundException, NoSuchMessageException {
        ExterniUzivatel externiUzivatel = externiUzivatelRepository.getDetailByUsername(username);

        if (externiUzivatel != null && Utils.getBcryptCheck(password, externiUzivatel.getPassword())) {
            return getDetail(externiUzivatel);
        } else {
            return null;
        }
    }

    private ExterniUzivatel getDetail(ExterniUzivatel externiUzivatel)
            throws RecordNotFoundException, NoSuchMessageException {
        if (externiUzivatel == null)
            return null;

        if (externiUzivatel.getRole() != null && externiUzivatel.getRole().size() > 0) {
            for (ExterniRole externiRole : externiUzivatel.getRole()) {
                externiRole.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        externiRole.getNazevResx()));
            }
        }

        return externiUzivatel;
    }

    public List<ExterniUzivatel> getList() throws RecordNotFoundException {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from ExterniUzivatel s");
        queryString.append(" where 1 = 1");

        queryString.append(" order by s.nazev ASC");

        Query vysledek = entityManager.createQuery(queryString.toString());

        @SuppressWarnings("unchecked")
        List<ExterniUzivatel> list = vysledek.getResultList();

        if (list != null && list.size() > 0) {
            for (ExterniUzivatel externiUzivatel : list) {
                externiUzivatel = getDetail(externiUzivatel);
            }
        }

        return list;
    }

    @TransactionalWrite
    public ExterniUzivatel save(ExterniUzivatel externiUzivatel)
            throws ValidationException, RecordNotFoundException, NoSuchMessageException,
            UniqueValueException {

        // kontrola jedinečnosti username
        Integer exist = externiUzivatelRepository.existsByUsername(externiUzivatel.getUsername(),
                Utils.toString(externiUzivatel.getIdExterniUzivatel()));
        if (exist > 0)
            throw new UniqueValueException(
                    messageSource.getMessage("username.unique", null, LocaleContextHolder.getLocale()),
                    externiUzivatel.getUsername(), true);
        if (StringUtils.isBlank(externiUzivatel.getIdExterniUzivatel())
                && StringUtils.isBlank(externiUzivatel.getPassword())) {
            externiUzivatel.setPassword(Utils.getBcrypt(externiUzivatel.getUsername()));
        }

        externiUzivatel.setCasZmn(Utils.getCasZmn());
        externiUzivatel.setZmenuProvedl(Utils.getZmenuProv());

        Utils.validate(externiUzivatel);
        externiUzivatel = externiUzivatelRepository.save(externiUzivatel);
        return externiUzivatel;
    }

    @TransactionalWrite
    public void odstranit(String idExterniUzivatel) {
        externiUzivatelRepository.zmenaAktivity(idExterniUzivatel, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    @TransactionalWrite
    public void obnovit(String idExterniUzivatel) {
        externiUzivatelRepository.zmenaAktivity(idExterniUzivatel, true, Utils.getCasZmn(),
                Utils.getZmenuProv());
    }
}
