package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.ZadostKlic;


public interface HistorieVypujcekRepository extends JpaRepository<HistorieVypujcek, String> {
    static final String sqlSelect = "select s from HistorieVypujcek s ";

    @Query(sqlSelect + "where s.zadostKlic = :zadostKlic")
    List<HistorieVypujcek> findByZadostKlic(ZadostKlic zadostKlic);


}
