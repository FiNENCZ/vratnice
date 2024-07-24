package cz.diamo.vratnice.zadosti.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.services.ZadostiExterniServices;
import cz.diamo.vratnice.dto.KlicDto;
import cz.diamo.vratnice.dto.PoschodiDto;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.Poschodi;
import cz.diamo.vratnice.repository.PoschodiRepository;
import cz.diamo.vratnice.service.KlicService;

@Service
@TransactionalROE
public class ZadostiServices extends ZadostiExterniServices {

    final static Logger logger = LogManager.getLogger(ZadostiServices.class);

    @Autowired
    private PoschodiRepository poschodiRepository;

    @Autowired
    private KlicService klicService;

    public List<PoschodiDto> seznamPoschodi(String idBudova, AppUserDto appUserDto) throws BaseException {
        List<Poschodi> poschodiList = poschodiRepository.getList(idBudova, true);

        List<PoschodiDto> poschodiDtos = new ArrayList<PoschodiDto>(poschodiList.size());
        for (Poschodi poschodi : poschodiList) {
            poschodiDtos.add(new PoschodiDto(poschodi));
        }

        return poschodiDtos;
    }

    public List<KlicDto> seznamKlic(String idLokalita, String idBudova,  String idPoschodi, AppUserDto appUserDto) throws BaseException {
        List<Klic> klicList = klicService.getList(idLokalita, idBudova, idPoschodi, true, null);

        List<KlicDto> klicDtos = new ArrayList<KlicDto>(klicList.size());
        for (Klic klic : klicList) {
            klicDtos.add(new KlicDto(klic));
        }

        return klicDtos;
    }


}
