package cz.diamo.vratnice.rest.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.rest.controller.BaseRestController;
import cz.diamo.share.services.LokalitaServices;
import cz.diamo.share.services.ZavodServices;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.dto.SpolecnostDto;
import cz.diamo.vratnice.dto.StatDto;
import cz.diamo.vratnice.dto.VozidloTypDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.repository.StatRepository;
import cz.diamo.vratnice.service.PovoleniVjezduVozidlaService;
import cz.diamo.vratnice.service.RidicService;
import cz.diamo.vratnice.service.SpolecnostService;
import cz.diamo.vratnice.service.StatService;
import cz.diamo.vratnice.service.VozidloTypService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class VratnicePublicRestController extends BaseRestController{

	final static Logger logger = LogManager.getLogger(VratnicePublicRestController.class);

	@Autowired
	private LokalitaServices lokalitaService;

    @Autowired
    private ZavodServices zavodServices;

    @Autowired
    private VozidloTypService vozidloTypService;

    @Autowired
    private StatService statService;

    @Autowired
    private StatRepository statRepository;

    @Autowired
    private RidicService ridicService;

    @Autowired
    private SpolecnostService spolecnostService;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private PovoleniVjezduVozidlaService povoleniVjezduVozidlaService;

    @GetMapping("/lokalita/list")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<List<LokalitaDto>> list(@RequestParam @Nullable String idZavod) {
        List<LokalitaDto> result = new ArrayList<LokalitaDto>();
        List<Lokalita> list = lokalitaService.getList(idZavod, true, true);

        if (list != null && list.size() > 0) {
            for (Lokalita lokalita : list) {
                result.add(new LokalitaDto(lokalita));
            }
        }

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/zavod/list")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
	public ResponseEntity<List<ZavodDto>> list(HttpServletRequest request, @RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idZavodu) {

		List<ZavodDto> result = new ArrayList<ZavodDto>();
		List<Zavod> list = zavodServices.getList(idZavodu, aktivni);
		
		if (list != null && list.size() > 0) {
			for (Zavod zavod : list) {
				result.add(new ZavodDto(zavod));
			}
		}

		return ResponseEntity.ok(result);
	}

    @GetMapping("/vozidlo-typ/list")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<List<VozidloTypDto>> list(@RequestParam @Nullable Boolean withIZS) {
        List<VozidloTypDto> result = new ArrayList<VozidloTypDto>();
        List<VozidloTyp> list = vozidloTypService.getList(withIZS);
        try {
            if (list != null && list.size() > 0) {
                for (VozidloTyp vozidloTyp : list) {
                    vozidloTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),vozidloTyp.getNazevResx()));
                    result.add(new VozidloTypDto(vozidloTyp));
                }
            } 
        }catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return ResponseEntity.ok(result);
    }

    @GetMapping("/vozidlo-typ/get-by-nazev")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<VozidloTypDto> getVozidloTypByNazev(@RequestParam String nazev) {
        VozidloTyp vozidloTyp = vozidloTypService.getByNazev(nazev);

        return ResponseEntity.ok(new VozidloTypDto(vozidloTyp));
    }

    @GetMapping("/stat/list")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public List<StatDto> list(HttpServletRequest request) {
        List<StatDto> result = new ArrayList<StatDto>();

        try {
			List<Stat> list = statRepository.findAll();
			if (list != null && list.size() > 0) {
				for (Stat stat : list) {
					stat.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							stat.getNazevResx()));
					result.add(new StatDto(stat));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }

    @GetMapping("/stat/get-by-nazev")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<StatDto> getStatByNazev(@RequestParam String nazev) {
        Stat stat = statService.getByNazev(nazev);

        return ResponseEntity.ok(new StatDto(stat));
    }

    @GetMapping("/ridic/get-by-cislo-op")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<RidicDto> getRidicByCisloOp(@RequestParam String cisloOp) {
        Ridic ridic = ridicService.getRidicByCisloOp(cisloOp);
        return ResponseEntity.ok(new RidicDto(ridic));
    }

    @PostMapping("/ridic/save")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<RidicDto> save(@RequestBody @Valid RidicDto ridicDto) throws UniqueValueException, NoSuchMessageException {
        Ridic newRidic = ridicService.create(ridicDto.toEntity());
        return ResponseEntity.ok(new RidicDto(newRidic));
    }

    @GetMapping("/spolecnost/list")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<List<SpolecnostDto>> spolecnostList() {
        List<SpolecnostDto> result = new ArrayList<SpolecnostDto>();
		List<Spolecnost> list = spolecnostService.getList();
		
		if (list != null && list.size() > 0) {
			for (Spolecnost spolecnost : list) {
				result.add(new SpolecnostDto(spolecnost));
			}
		}

		return ResponseEntity.ok(result);
    }

    @GetMapping("/spolecnost/get-by-nazev")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<SpolecnostDto> getSpolecnostByNazev(@RequestParam String nazev) {
        Spolecnost spolecnost = spolecnostService.getByNazev(nazev);
        return ResponseEntity.ok(new SpolecnostDto(spolecnost));
    }
    
    
    @PostMapping("/spolecnost/save")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<SpolecnostDto> save(@RequestBody @Valid SpolecnostDto spolecnostDto) {
        Spolecnost savedSpolecnost = spolecnostService.save(spolecnostDto.toEntity());
        return ResponseEntity.ok(new SpolecnostDto(savedSpolecnost));
    }


    @PostMapping("/povoleni-vjezdu-vozidla/save")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_PUBLIC')")
    public ResponseEntity<PovoleniVjezduVozidlaDto> save(HttpServletRequest request, @RequestBody @Valid PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) throws NoSuchMessageException, BaseException {
        PovoleniVjezduVozidla savedPovoleni = povoleniVjezduVozidlaService.createFromPublic(povoleniVjezduVozidlaDto, request);
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(savedPovoleni));
    }

}
