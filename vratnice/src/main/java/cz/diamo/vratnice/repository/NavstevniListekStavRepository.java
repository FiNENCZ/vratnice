package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.NavstevniListekStav;

public interface NavstevniListekStavRepository extends JpaRepository<NavstevniListekStav, Integer> {
    static final String sqlSelect = "select s from NavstevniListekStav s ";

    @Query(sqlSelect + "where s.idNavstevniListekStav = :idNavstevniListekStav")
    NavstevniListekStav getDetail(Integer idNavstevniListekStav);

}
