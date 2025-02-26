package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.NavstevniListek;

public interface NavstevniListekRepository extends JpaRepository<NavstevniListek, String> {
    static final String sqlSelect = "select s from NavstevniListek s ";

    @Query(sqlSelect + "where s.idNavstevniListek = :idNavstevniListek")
    NavstevniListek getDetail(String idNavstevniListek);
}
