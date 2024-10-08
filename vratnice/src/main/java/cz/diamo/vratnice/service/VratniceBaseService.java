package cz.diamo.vratnice.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.avizace.AvizaceEmailRequestDto;
import cz.diamo.share.dto.avizace.AvizaceOznameniRequestDto;
import cz.diamo.share.dto.avizace.AvizacePrijemceRequestDto;
import cz.diamo.share.dto.avizace.AvizaceRequestDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.enums.RoleEnum;
import cz.diamo.share.enums.TypOznameniEnum;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.OznameniServices;
import cz.diamo.vratnice.entity.Vratnice;
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

    @TransactionalRO
    public void zaslatOznameniUzivateli(String predmet, String oznameniText, String teloEmailu, String url,
            List<Uzivatel> prijemci, TypOznameniEnum typOznameniEnum, HttpServletRequest request) throws NoSuchMessageException, BaseException {

        AvizaceRequestDto avizaceRequestDto = new AvizaceRequestDto();
        avizaceRequestDto.setEmail(new AvizaceEmailRequestDto(predmet, teloEmailu, null));
        avizaceRequestDto.setOznameni(new AvizaceOznameniRequestDto(typOznameniEnum, predmet, oznameniText, url));

        //Přidat příjemce
        for (Uzivatel uzivatelOznameni: prijemci) {
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

    public List<Uzivatel> listUzivateleDleOpravneniKZavoduARoliProCelyPodnik(List<RoleEnum> role, String idZavod) {
        // HQL dotaz na výběr uživatelů s odpovídajícími rolemi, modulem "vratnice" a přístupem k danému závodu
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

    public List<Uzivatel> getUzivateleDleNastaveneVratnice(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        String hql = "SELECT u FROM Uzivatel u " +
        "WHERE u.zavod.idZavod = :idZavod " +
        "AND u.aktivita = true";

        TypedQuery<Uzivatel> query = entityManager.createQuery(hql, Uzivatel.class);
        query.setParameter("idZavod", nastavenaVratnice.getZavod().getIdZavod());

        return query.getResultList();
    }

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
