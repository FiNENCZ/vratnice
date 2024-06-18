package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Klic;

public interface KlicRepository extends JpaRepository<Klic, String>{
    static final String sqlSelect = "select s from Klic s ";


    @Query(sqlSelect + "where s.idKey = :idKey")
    Klic getDetail(String idKey);

    @Query(sqlSelect + "where s.chipCode = :chipCode")
    Klic getDetailByChipCode(String chipCode);

    @Query(sqlSelect + "where s.special = :special")
    List<Klic> getBySpecialni(Boolean special);

    @Query(sqlSelect + "where s.aktivita = :aktivita")
    List<Klic> findByAktivita(Boolean aktivita);

    

}
