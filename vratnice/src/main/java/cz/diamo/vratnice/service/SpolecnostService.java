package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.repository.SpolecnostRepository;

@Service
public class SpolecnostService {

    @Autowired
    private SpolecnostRepository spolecnostRepository;

    /**
     * Ukládá společnost do databáze. Pokud společnost již existuje na základě
     * názvu, vrátí existující záznam.
     *
     * @param spolecnost Objekt {@link Spolecnost}, který se má uložit.
     * @return Uložený objekt {@link Spolecnost} nebo existující objekt, pokud již
     *         existuje.
     */
    public Spolecnost save(Spolecnost spolecnost) {
        if (spolecnost.getIdSpolecnost() == null || spolecnost.getIdSpolecnost().isEmpty()) {
            if (spolecnostRepository.getByNazev(spolecnost.getNazev()) == null) {
                return spolecnostRepository.save(spolecnost);
            } else {
                return spolecnostRepository.getByNazev(spolecnost.getNazev());
            }
        }
        return spolecnost;
    }

    /**
     * Vrací seznam všech společností uložených v databázi.
     *
     * @return Seznam {@link Spolecnost} obsahující všechny společnosti.
     */
    public List<Spolecnost> getList() {
        return spolecnostRepository.findAll();
    }

    /**
     * Vrací společnost na základě jejího názvu.
     *
     * @param nazev Název společnosti, kterou se má vyhledat.
     * @return Objekt {@link Spolecnost} odpovídající danému názvu.
     */
    public Spolecnost getByNazev(String nazev) {
        return spolecnostRepository.getByNazev(nazev);
    }

}
