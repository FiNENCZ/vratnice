package cz.dp.vratnice.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalRO;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.avizace.AvizaceEmailRequestDto;
import cz.dp.share.dto.avizace.AvizaceOznameniRequestDto;
import cz.dp.share.dto.avizace.AvizacePrijemceRequestDto;
import cz.dp.share.dto.avizace.AvizaceRequestDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.Zavod;
import cz.dp.share.enums.RoleEnum;
import cz.dp.share.enums.TypOznameniEnum;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.services.OznameniServices;
import cz.dp.vratnice.entity.Vratnice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class VratniceBaseService {

    @Autowired
    private OznameniServices oznameniServices;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Odesílá oznámení uživatelům na základě zadaných parametrů.
     *
     * @param predmet         Předmět oznámení.
     * @param oznameniText    Text oznámení.
     * @param teloEmailu      Tělo emailu.
     * @param url             URL, na které se má odkazovat v oznámení.
     * @param prijemci        Seznam příjemců typu {@link Uzivatel}.
     * @param typOznameniEnum Typ oznámení jako {@link TypOznameniEnum}.
     * @param request         HTTP požadavek.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při zpracování oznámení.
     */
    @TransactionalRO
    public void zaslatOznameniUzivateli(String predmet, String oznameniText, String teloEmailu, String url,
            List<Uzivatel> prijemci, TypOznameniEnum typOznameniEnum, HttpServletRequest request)
            throws NoSuchMessageException, BaseException {

        AvizaceRequestDto avizaceRequestDto = new AvizaceRequestDto();
        avizaceRequestDto.setEmail(new AvizaceEmailRequestDto(predmet, teloEmailu, null));
        avizaceRequestDto.setOznameni(new AvizaceOznameniRequestDto(typOznameniEnum, predmet, oznameniText, url));

        // Přidat příjemce
        for (Uzivatel uzivatelOznameni : prijemci) {
            String email = uzivatelOznameni.getEmail();
            String sapId = uzivatelOznameni.getSapId();
            AvizacePrijemceRequestDto prijemnce = new AvizacePrijemceRequestDto();

            if (sapId != null)
                prijemnce.setSapId(sapId);

            if (email != null)
                prijemnce.setEmail(email);

            avizaceRequestDto.pridatPrijemce(prijemnce);
        }

        oznameniServices.save(avizaceRequestDto, request);

    }

    /**
     * Vrací seznam závodů, ke kterým má uživatel přístup na základě jeho ID a
     * aktivity.
     *
     * @param idUzivatel ID uživatele, jehož závody se mají vrátit.
     * @param aktivita   Boolean hodnota.
     * @return Seznam objektů {@link Zavod} odpovídajících zadaným parametrům.
     */
    public List<Zavod> getAllZavodyUzivateleByPristup(String idUzivatel, Boolean aktivita) {
        String hql = "SELECT DISTINCT z FROM Zavod z " +
                "LEFT JOIN UzivatelZavod uz ON z.idZavod = uz.idZavod " +
                "LEFT JOIN OpravneniZavod oz ON z.idZavod = oz.idZavod " +
                "LEFT JOIN Opravneni o ON oz.idOpravneni = o.idOpravneni " +
                "LEFT JOIN UzivatelOpravneni uo ON o.idOpravneni = uo.idOpravneni " +
                "WHERE (uz.idUzivatel = :idUzivatel OR uo.idUzivatel = :idUzivatel) ";

        if (aktivita != null)
            hql += "AND z.aktivita = :aktivita";

        TypedQuery<Zavod> query = entityManager.createQuery(hql, Zavod.class);
        query.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            query.setParameter("aktivita", aktivita);

        return query.getResultList();
    }

    /**
     * Vrací seznam uživatelů s odpovídajícími oprávněními k danému závodu a roli
     * pro celý podnik.
     *
     * @param role    Seznam rolí typu {@link RoleEnum}, které se mají zohlednit.
     * @param idZavod ID závodu, pro který se mají uživatelé vyhledat.
     * @return Seznam uživatelů typu {@link Uzivatel} odpovídajících zadaným
     *         parametrům.
     */
    public List<Uzivatel> listUzivateleDleOpravneniKZavoduARoliProCelyPodnik(List<RoleEnum> role, String idZavod) {
        // HQL dotaz na výběr uživatelů s odpovídajícími rolemi, modulem "vratnice" a
        // přístupem k danému závodu
        String hql = "SELECT u FROM Uzivatel u " +
                "JOIN UzivatelModul um ON u.idUzivatel = um.idUzivatel " +
                "JOIN UzivatelOpravneni uo ON u.idUzivatel = uo.idUzivatel " +
                "JOIN Opravneni o ON uo.idOpravneni = o.idOpravneni " +
                "JOIN OpravneniRole orl ON o.idOpravneni = orl.idOpravneni " +
                "JOIN OpravneniZavod oz ON o.idOpravneni = oz.idOpravneni " +
                "WHERE orl.authority IN :roles " +
                "AND um.modul = 'vratnice' " +
                "AND oz.idZavod = :idZavod";

        TypedQuery<Uzivatel> query = entityManager.createQuery(hql, Uzivatel.class);
        query.setParameter("roles", role.stream().map(RoleEnum::toString).toList()); // Převod RoleEnum na String
        query.setParameter("idZavod", idZavod);

        return query.getResultList();
    }

    /**
     * Vrací seznam uživatelů podle nastavené vratnice pro daného uživatele.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o uživateli.
     * @return Seznam uživatelů typu {@link Uzivatel} odpovídajících nastavené
     *         vratnici.
     * @throws RecordNotFoundException Pokud nebyla nalezena vratnice pro uživatele.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<Uzivatel> getUzivateleDleNastaveneVratnice(AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        String hql = "SELECT u FROM Uzivatel u " +
                "WHERE u.zavod.idZavod = :idZavod " +
                "AND u.aktivita = true";

        TypedQuery<Uzivatel> query = entityManager.createQuery(hql, Uzivatel.class);
        query.setParameter("idZavod", nastavenaVratnice.getZavod().getIdZavod());

        return query.getResultList();
    }

    /**
     * Vrací seznam zastupujících uživatelů pro daného uživatele.
     *
     * @param idUzivatel ID uživatele, pro kterého se mají vrátit zastupující
     *                   uživatelé.
     * @return Seznam zastupujících uživatelů typu {@link Uzivatel}.
     */
    public List<Uzivatel> getZastupujiciUzivatele(String idUzivatel) {
        String hql = "SELECT u FROM Uzivatel u " +
                "JOIN Zastup z ON z.uzivatelZastupce.idUzivatel = u.idUzivatel " +
                "WHERE z.uzivatel.idUzivatel = :idUzivatel " +
                "AND z.platnostOd <= :currentDate " +
                "AND z.platnostDo >= :currentDate " +
                "AND z.aktivita = true";

        TypedQuery<Uzivatel> query = entityManager.createQuery(hql, Uzivatel.class);
        query.setParameter("idUzivatel", idUzivatel);
        query.setParameter("currentDate", new Date());

        return query.getResultList();
    }
}
