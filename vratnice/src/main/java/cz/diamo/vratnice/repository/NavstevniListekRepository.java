package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;

public interface NavstevniListekRepository extends JpaRepository<NavstevniListek, String> {
    static final String sqlSelect = "select s from NavstevniListek s ";

    @Query(sqlSelect + "where s.idNavstevniListek = :idNavstevniListek")
    NavstevniListek getDetail(String idNavstevniListek);

    @Query("select nl from NavstevniListek nl join nl.uzivatel u where u = :uzivatel")
    List<NavstevniListek> findByUzivatel(Uzivatel uzivatel);

    @Query("select nl from NavstevniListek nl join nl.navstevaOsoba no where no = :navstevaOsoba")
    List<NavstevniListek> findByNavstevaOsoba(NavstevaOsoba navstevaOsoba);
}
