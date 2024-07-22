package cz.diamo.vratnice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.services.ZavodServices;
import cz.diamo.vratnice.csvRepresentation.PovoleniVjezduVozidlaCsvRepresentation;
import cz.diamo.vratnice.csvRepresentation.RzTypVozidlaCsvRepresentation;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.dto.RzTypVozidlaDto;
import cz.diamo.vratnice.dto.StatDto;
import cz.diamo.vratnice.dto.VozidloTypDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

@Service
public class PovoleniVjezduVozidlaService {

    final static Logger logger = LogManager.getLogger(PovoleniVjezduVozidlaService.class);

    @Autowired
    private Validator validator;
    @Autowired
    private PovoleniVjezduVozidlaRepository povoleniVjezduVozidlaRepository;

    @Autowired
    private RidicService ridicService;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private VozidloTypService vozidloTypService;

    @Autowired
    private StatService statService;

    @Autowired
    private ZavodServices zavodServices;

    public List<PovoleniVjezduVozidla> getAll() {
        return povoleniVjezduVozidlaRepository.findAll();
    }

    public PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
    }

    public List<PovoleniVjezduVozidla> getByStav(String stav) {
        return povoleniVjezduVozidlaRepository.getByStav(stav);
    }

    public List<PovoleniVjezduVozidla> getByRzVozidla(String rzVozidla) {
        return povoleniVjezduVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public Optional<PovoleniVjezduVozidla> jeRzVozidlaPovolena(String rzVozidla) {
        List<PovoleniVjezduVozidla> povoleniVjezduVozidlaList = getByRzVozidla(rzVozidla);

        if (povoleniVjezduVozidlaList.isEmpty()) {
            return Optional.empty();
        }

        Date currentDate = new Date();
        for (PovoleniVjezduVozidla povoleniVjezduVozidla : povoleniVjezduVozidlaList) {
            if (currentDate.compareTo(povoleniVjezduVozidla.getDatumOd()) >= 0 
                    && currentDate.compareTo(povoleniVjezduVozidla.getDatumDo()) <= 0) {
                return Optional.of(povoleniVjezduVozidla);
            }
        }

        return Optional.empty();
    }

    @Transactional
    public PovoleniVjezduVozidla create(PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) {
        if (povoleniVjezduVozidlaDto.getRidic() != null) {
            Ridic savedRidic =  ridicService.create(povoleniVjezduVozidlaDto.getRidic().toEntity());
            povoleniVjezduVozidlaDto.setRidic(new RidicDto(savedRidic));
        }

        return povoleniVjezduVozidlaRepository.save(povoleniVjezduVozidlaDto.toEntity());
    }

    public Stat getZemeRegistraceVozidla(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
        try {
            if (povoleniVjezduVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            povoleniVjezduVozidla.getZemeRegistraceVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleniVjezduVozidla.getZemeRegistraceVozidla().getNazevResx()));
            return povoleniVjezduVozidla.getZemeRegistraceVozidla();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public Set<PovoleniVjezduVozidlaDto>  processPovoleniCsvData(MultipartFile file) throws IOException, ParseException {
        Set<PovoleniVjezduVozidlaDto> povoleniVjezduVozidlas = parsePovoleniCsv(file);
        Set<PovoleniVjezduVozidlaDto> savedDtos = new HashSet<>();
    
        for (PovoleniVjezduVozidlaDto dto : povoleniVjezduVozidlas) {
            PovoleniVjezduVozidla savedEntity = create(dto); 
            savedDtos.add(new PovoleniVjezduVozidlaDto(savedEntity));
        }
    
        return savedDtos;
    }

    private Set<PovoleniVjezduVozidlaDto> parsePovoleniCsv(MultipartFile file) throws IOException, ParseException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Set<PovoleniVjezduVozidlaDto> povoleniVjezduVozidlaSet = new HashSet<>();
            HeaderColumnNameMappingStrategy<PovoleniVjezduVozidlaCsvRepresentation> strategy =
                new HeaderColumnNameMappingStrategy<>();

            strategy.setType(PovoleniVjezduVozidlaCsvRepresentation.class);
            CsvToBean<PovoleniVjezduVozidlaCsvRepresentation> csvToBean = 
                new CsvToBeanBuilder<PovoleniVjezduVozidlaCsvRepresentation>(reader)
                    .withMappingStrategy(strategy).withIgnoreEmptyLine(true).withIgnoreLeadingWhiteSpace(true).build();

            for (PovoleniVjezduVozidlaCsvRepresentation csvLine : csvToBean.parse()) {
                PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto = new PovoleniVjezduVozidlaDto();

                // Basic fields
                povoleniVjezduVozidlaDto.setJmenoZadatele(csvLine.getJmenoZadatele());
                povoleniVjezduVozidlaDto.setPrijmeniZadatele(csvLine.getPrijmeniZadatele());
                povoleniVjezduVozidlaDto.setSpolecnostZadatele(csvLine.getSpolecnostZadatele());
                povoleniVjezduVozidlaDto.setIcoZadatele(csvLine.getIcoZadatele());
                povoleniVjezduVozidlaDto.setDuvodZadosti(csvLine.getDuvodZadosti());
                povoleniVjezduVozidlaDto.setZemeRegistraceVozidla(new StatDto(statService.getByNazev(csvLine.getZemeRegistraceVozidla())));

                // Handle multiple values separated by '|'
                if (csvLine.getRzVozidla() != null && csvLine.getRzVozidla().length > 0) {
                    povoleniVjezduVozidlaDto.setRzVozidla(List.of(csvLine.getRzVozidla()));
                }

                if (csvLine.getTypVozidla() != null && csvLine.getTypVozidla().length > 0) {
                    List<VozidloTypDto> typVozidlaList = new ArrayList<>();
                    for (String typ : csvLine.getTypVozidla()) {
                        typVozidlaList.add(new VozidloTypDto(vozidloTypService.getByNazev(typ)));
                    }
                    povoleniVjezduVozidlaDto.setTypVozidla(typVozidlaList);
                }

                if(csvLine.getRidic_jmeno() != null && csvLine.getRidic_prijmeni() != null && csvLine.getRidic_cisloOp() != null ) {
                    RidicDto ridicDto = new RidicDto();

                    Ridic ridic = ridicService.getRidicByCisloOp(csvLine.getRidic_cisloOp());
                    if (ridic == null) {
                        ridicDto.setJmeno(csvLine.getRidic_jmeno());
                        ridicDto.setPrijmeni(csvLine.getRidic_prijmeni());
                        ridicDto.setCisloOp(csvLine.getRidic_cisloOp());
                        ridicDto.setFirma(csvLine.getRidic_firma());
                    } else {
                        ridicDto = new RidicDto(ridic);
                    }

                    povoleniVjezduVozidlaDto.setRidic(ridicDto);
                }

                povoleniVjezduVozidlaDto.setSpolecnostVozidla(csvLine.getSpolecnostVozidla());
                povoleniVjezduVozidlaDto.setDatumOd(parseDate(csvLine.getDatumOd()));
                povoleniVjezduVozidlaDto.setDatumDo(parseDate(csvLine.getDatumDo()));

                List<ZavodDto> zavodDtos = new ArrayList<ZavodDto>();
                List<Zavod> list = zavodServices.getList(null, null);
                
                if (list != null && list.size() > 0) {
                    for (Zavod zavod : list) {
                        zavodDtos.add(new ZavodDto(zavod));
                    }
                }

                if (csvLine.getZavod_nazvy() != null && csvLine.getZavod_nazvy().length > 0) {
                    List<ZavodDto> zavodyList = new ArrayList<>();
                    for (String zavod : csvLine.getZavod_nazvy()) {
                        for (ZavodDto zavodDto: zavodDtos) {
                            if (zavodDto.getNazev() == zavod) {
                                zavodyList.add(zavodDto);
                            }
                        }
                    }
                    povoleniVjezduVozidlaDto.setZavod(zavodyList);
                }
                povoleniVjezduVozidlaDto.setOpakovanyVjezd(csvLine.isOpakovanyVjezd());

                validate(povoleniVjezduVozidlaDto);

                // Add to set
                povoleniVjezduVozidlaSet.add(povoleniVjezduVozidlaDto);
            }
            return povoleniVjezduVozidlaSet;
        }
     }


    public RzTypVozidlaDto processRzTypVozidlaCsvData(MultipartFile file) throws IOException, ParseException {
        Set<RzTypVozidlaDto> rzTypVozidlaSet = parseRzTypVozidlaCsv(file);

        RzTypVozidlaDto aggregatedRzTypVozidlaDto = new RzTypVozidlaDto();
        List<String> aggregatedRzVozidla = new ArrayList<>();
        List<VozidloTypDto> aggregatedTypVozidla = new ArrayList<>();
    
        for (RzTypVozidlaDto rzTypVozidlaDto : rzTypVozidlaSet) {
            if (rzTypVozidlaDto.getRzVozidla() != null) {
                aggregatedRzVozidla.addAll(rzTypVozidlaDto.getRzVozidla());
            }
            if (rzTypVozidlaDto.getTypVozidla() != null) {
                aggregatedTypVozidla.addAll(rzTypVozidlaDto.getTypVozidla());
            }
        }
    
        aggregatedRzTypVozidlaDto.setRzVozidla(aggregatedRzVozidla);
        aggregatedRzTypVozidlaDto.setTypVozidla(aggregatedTypVozidla);
    
        // Můžete přidat aggregatedRzTypVozidlaDto do výsledné množiny, pokud to dává smysl
        rzTypVozidlaSet.add(aggregatedRzTypVozidlaDto);

    
        return aggregatedRzTypVozidlaDto;
    }

    private Set<RzTypVozidlaDto> parseRzTypVozidlaCsv(MultipartFile file) throws IOException, ParseException {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Set<RzTypVozidlaDto> rzTypVozidlaSet = new HashSet<>();
            HeaderColumnNameMappingStrategy<RzTypVozidlaCsvRepresentation> strategy =
                new HeaderColumnNameMappingStrategy<>();

            strategy.setType(RzTypVozidlaCsvRepresentation.class);
            CsvToBean<RzTypVozidlaCsvRepresentation> csvToBean = 
                new CsvToBeanBuilder<RzTypVozidlaCsvRepresentation>(reader)
                    .withMappingStrategy(strategy).withIgnoreEmptyLine(true).withIgnoreLeadingWhiteSpace(true).build();

            for (RzTypVozidlaCsvRepresentation csvLine : csvToBean.parse()) {
                RzTypVozidlaDto rzTypVozidla = new RzTypVozidlaDto();


                // Handle multiple values separated by '|'
                if (csvLine.getRzVozidla() != null && csvLine.getRzVozidla().length > 0) {
                    rzTypVozidla.setRzVozidla(List.of(csvLine.getRzVozidla()));
                }

                if (csvLine.getTypVozidla() != null && csvLine.getTypVozidla().length > 0) {
                    List<VozidloTypDto> typVozidlaList = new ArrayList<>();
                    for (String typ : csvLine.getTypVozidla()) {
                        typVozidlaList.add(new VozidloTypDto(vozidloTypService.getByNazev(typ)));
                    }
                    rzTypVozidla.setTypVozidla(typVozidlaList);
                }

                // Add to set
                rzTypVozidlaSet.add(rzTypVozidla);
            }
            return rzTypVozidlaSet;
        }
     }

     private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.parse(dateStr);
    }

    private void validate(@Valid PovoleniVjezduVozidlaDto dto) {
        Set<ConstraintViolation<PovoleniVjezduVozidlaDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }



}
