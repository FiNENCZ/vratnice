package cz.diamo.vratnice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import cz.diamo.vratnice.repository.VyjezdVozidlaRepository;
import jakarta.transaction.Transactional;

@Service
public class VyjezdVozidlaService {

    @Autowired
    private VyjezdVozidlaRepository vyjezdVozidlaRepository;

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    public List<VyjezdVozidla> getAll() {
        return vyjezdVozidlaRepository.findAll();
    }

    public VyjezdVozidla getDetail(String idVyjezdVozidla) {
        return vyjezdVozidlaRepository.getDetail(idVyjezdVozidla);
    }

    public List<VyjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public Optional<VyjezdVozidla> jeMozneVyjet(String rzVozidla) {
        List<VjezdVozidla> vjezdVozidel = vjezdVozidlaRepository.getByRzVozidla(rzVozidla);
        List<VyjezdVozidla> vyjezdVozidel = vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    
        if (vjezdVozidel.isEmpty()) {
            return Optional.empty();
        }
    
        VjezdVozidla posledniVjezdVozidla = vjezdVozidel.get(vjezdVozidel.size() - 1);
    
        if (vjezdVozidel.size() > vyjezdVozidel.size() || vyjezdVozidel.isEmpty()) {
            return Optional.of(mapToVyjezdVozidla(posledniVjezdVozidla));
        }
    
        return Optional.empty();
    }
    
    private VyjezdVozidla mapToVyjezdVozidla(VjezdVozidla posledniVjezdVozidla) {
        VyjezdVozidla vyjezdVozidla = new VyjezdVozidla();
        
        if (posledniVjezdVozidla.getOpakovanyVjezd() != null) {
            vyjezdVozidla.setOpakovanyVjezd(true);
        }
        
        vyjezdVozidla.setRzVozidla(posledniVjezdVozidla.getRzVozidla());
        return vyjezdVozidla;
    }

    @Transactional
    public VyjezdVozidla create(VyjezdVozidla vyjezdVozidla) {
        vyjezdVozidla.setCasZmn(Utils.getCasZmn());
        vyjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        return vyjezdVozidlaRepository.save(vyjezdVozidla);
    }


}
