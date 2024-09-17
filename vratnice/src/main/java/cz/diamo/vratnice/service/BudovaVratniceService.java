package cz.diamo.vratnice.service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.entity.Budova;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.enums.OpravneniTypPristupuBudovaEnum;
import cz.diamo.share.repository.OpravneniBudovaRepository;
import cz.diamo.share.repository.OpravneniZavodRepository;
import cz.diamo.share.repository.UzivatelOpravneniRepository;
import cz.diamo.share.services.BudovaServices;

@Service
public class BudovaVratniceService {

    @Autowired
	private BudovaServices budovaServices;

    @Autowired
	private OpravneniBudovaRepository opravneniBudovaRepository;

	@Autowired
	private OpravneniZavodRepository opravneniZavodRepository;

    @Autowired
	private UzivatelOpravneniRepository uzivatelOpravneniRepository;

    public Set<BudovaDto> listVsechnyBudovy(Boolean aktivita, String idLokalita) {
        Set<BudovaDto> resultBudovy = new HashSet<>();
        List<Budova> vsechnyBudovy = budovaServices.getList(null, idLokalita, aktivita);
        for (Budova budova : vsechnyBudovy) {
            resultBudovy.add(new BudovaDto(budova));
        }
        return resultBudovy;
    }
    
    public Set<BudovaDto> listBudovyZavodu(Opravneni opravneni, Boolean aktivita, String idLokalita) {
        Set<BudovaDto> resultBudovy = new HashSet<>();
        List<Zavod> zavodyDleOpravneni = opravneniZavodRepository.listZavod(opravneni.getIdOpravneni());
        for (Zavod zavod : zavodyDleOpravneni) {
            List<Budova> budovyZavodu = budovaServices.getList(zavod.getIdZavod(), idLokalita, aktivita);
            for (Budova budova : budovyZavodu) {
                resultBudovy.add(new BudovaDto(budova));
            }
        }
        return resultBudovy;
    }
    
    public Set<BudovaDto> listVybraneBudovy(Opravneni opravneni, Boolean aktivita, String idLokalita) {
        Set<BudovaDto> resultBudovy = new HashSet<>();
        List<Budova> vybraneBudovy = opravneniBudovaRepository.listBudova(opravneni.getIdOpravneni());
        if (vybraneBudovy != null) {
            for (Budova budova : vybraneBudovy) {
                if (aktivita != null) {
                    if (budova.getAktivita().equals(aktivita) && budova.getLokalita().getIdLokalita().equals(idLokalita)) {
                        resultBudovy.add(new BudovaDto(budova));
                    }
                }
            }
        }
        return resultBudovy;
    }

	public boolean maUzivatelPristupKBudove(Budova budova, AppUserDto appUserDto) {
		String idUzivatel = appUserDto.getIdUzivatel();
		
		List<Opravneni> opravneniUzivatele = uzivatelOpravneniRepository.listOpravneni(idUzivatel, true);
	
		if (opravneniUzivatele == null || opravneniUzivatele.isEmpty()) {
			return false;  
		}
	
		for (Opravneni opravneni : opravneniUzivatele) {
			OpravneniTypPristupuBudovaEnum typPristupu = opravneni.getOpravneniTypPristupuBudova().getOpravneniTypPristupuBudovaEnum();
	
			switch (typPristupu) {
				case TYP_PRIST_BUDOVA_OPR_VSE:
					return true;
	
				case TYP_PRIST_BUDOVA_OPR_ZAVOD:
					List<Zavod> zavodyDleOpravneni = opravneniZavodRepository.listZavod(opravneni.getIdOpravneni());
					for (Zavod zavod : zavodyDleOpravneni) {
						if (budova.getLokalita().getZavod().getIdZavod().equals(zavod.getIdZavod())) {
							return true;
						}
					}
					break;
	
				case TYP_PRIST_BUDOVA_OPR_VYBER:
					List<Budova> vybraneBudovy = opravneniBudovaRepository.listBudova(opravneni.getIdOpravneni());
					for (Budova vybranaBudova : vybraneBudovy) {
						if (vybranaBudova.getIdBudova().equals(budova.getIdBudova())) {
							return true;
						}
					}
					break;
	
				default:
					break;
			}
		}
		
		return false;
	}	

}
