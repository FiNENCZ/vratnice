package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Klic;

public interface KlicRepository extends JpaRepository<Klic, String>{
    static final String sqlSelect = "select s from Klic s ";


    @Query(sqlSelect + "where s.idKlic = :idKlic")
    Klic getDetail(String idKlic);

    @Query(sqlSelect + "where s.kodCipu = :kodCipu")
    Klic getDetailByKodCipu(String kodCipu);

    @Query(sqlSelect + "where s.specialni = :specialni")
    List<Klic> getBySpecialni(Boolean specialni);

    @Query(sqlSelect + "where s.aktivita = :aktivita")
    List<Klic> findByAktivita(Boolean aktivita);

    

}
