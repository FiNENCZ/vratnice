package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.NavstevniListekTyp;

public interface NavstevniListekTypRepository extends JpaRepository<NavstevniListekTyp, Integer> {
    static final String sqlSelect = "select s from NavstevniListekTyp s ";

    @Query(sqlSelect + "where s.idNavstevniListekTyp = :idNavstevniListekTyp")
    NavstevniListekTyp getDetail(Integer idNavstevniListekTyp);


}
