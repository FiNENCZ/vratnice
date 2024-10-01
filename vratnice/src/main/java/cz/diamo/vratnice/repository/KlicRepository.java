package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Klic;

public interface KlicRepository extends JpaRepository<Klic, String>{
    static final String sqlSelect = "select s from Klic s ";


    @Query(sqlSelect + "where s.idKlic = :idKlic")
    Klic getDetail(String idKlic);

}
