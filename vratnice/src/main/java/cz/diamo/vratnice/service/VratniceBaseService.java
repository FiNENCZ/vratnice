package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.dto.avizace.AvizaceEmailRequestDto;
import cz.diamo.share.dto.avizace.AvizaceOznameniRequestDto;
import cz.diamo.share.dto.avizace.AvizacePrijemceRequestDto;
import cz.diamo.share.dto.avizace.AvizaceRequestDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.enums.TypOznameniEnum;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.services.OznameniServices;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class VratniceBaseService {

    @Autowired
    private OznameniServices oznameniServices;


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


}
