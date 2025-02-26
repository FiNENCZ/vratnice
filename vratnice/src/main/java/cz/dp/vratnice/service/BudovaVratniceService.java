package cz.dp.vratnice.service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.BudovaDto;
import cz.dp.share.entity.Budova;
import cz.dp.share.entity.Opravneni;
import cz.dp.share.entity.Zavod;
import cz.dp.share.enums.OpravneniTypPristupuBudovaEnum;
import cz.dp.share.repository.OpravneniBudovaRepository;
import cz.dp.share.repository.OpravneniZavodRepository;
import cz.dp.share.repository.UzivatelOpravneniRepository;
import cz.dp.share.services.BudovaServices;

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

	/**
	 * Vrátí seznam všech budov.
	 *
	 * @param aktivita
	 * @param idLokalita ID lokality.
	 * @return Set {@link BudovaDto} objektů. Prázdný set, pokud nejsou nalezeny
	 *         žádné budovy.
	 */
	public Set<BudovaDto> listVsechnyBudovy(Boolean aktivita, String idLokalita) {
		Set<BudovaDto> resultBudovy = new HashSet<>();
		List<Budova> vsechnyBudovy = budovaServices.getList(null, idLokalita, aktivita);
		for (Budova budova : vsechnyBudovy) {
			resultBudovy.add(new BudovaDto(budova));
		}
		return resultBudovy;
	}

	/**
	 * Vrátí seznam budov zavodu
	 *
	 * @param opravneni
	 * @param aktivita
	 * @param idLokalita
	 * @return Set {@link BudovaDto} objektů. Prázdný set, pokud nejsou nalezeny
	 *         žádné budovy.
	 */
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

	/**
	 * Vrátí seznam vybraných budov.
	 *
	 * @param opravneni
	 * @param aktivita
	 * @param idLokalita
	 * @return Set {@link BudovaDto} objektů. Prázdný set, pokud nejsou nalezeny
	 *         žádné budovy splňující zadaná kritéria.
	 */
	public Set<BudovaDto> listVybraneBudovy(Opravneni opravneni, Boolean aktivita, String idLokalita) {
		Set<BudovaDto> resultBudovy = new HashSet<>();
		List<Budova> vybraneBudovy = opravneniBudovaRepository.listBudova(opravneni.getIdOpravneni());
		if (vybraneBudovy != null) {
			for (Budova budova : vybraneBudovy) {
				if (aktivita != null) {
					if (budova.getAktivita().equals(aktivita)
							&& budova.getLokalita().getIdLokalita().equals(idLokalita)) {
						resultBudovy.add(new BudovaDto(budova));
					}
				}
			}
		}
		return resultBudovy;
	}

	/**
	 * Kontrola, zda má uživatel přístup k budově.
	 *
	 * @param budova     Budova, ke které se kontroluje přístup.
	 * @param appUserDto Uživatel, jehož přístup se kontroluje.
	 * @return true, pokud má uživatel přístup k budově; jinak false.
	 */
	public boolean maUzivatelPristupKBudove(Budova budova, AppUserDto appUserDto) {
		String idUzivatel = appUserDto.getIdUzivatel();

		List<Opravneni> opravneniUzivatele = uzivatelOpravneniRepository.listOpravneni(idUzivatel, true);

		if (opravneniUzivatele == null || opravneniUzivatele.isEmpty()) {
			return false;
		}

		for (Opravneni opravneni : opravneniUzivatele) {
			OpravneniTypPristupuBudovaEnum typPristupu = opravneni.getOpravneniTypPristupuBudova()
					.getOpravneniTypPristupuBudovaEnum();

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
