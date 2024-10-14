package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Spolecnost;

public interface SpolecnostRepository extends JpaRepository<Spolecnost, String> {
    static final String sqlSelect = "select s from Spolecnost s ";

    @Query(sqlSelect + "where s.nazev = :nazev")
    Spolecnost getByNazev(String nazev);

}
