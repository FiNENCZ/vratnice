package cz.dp.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.HistorieKlic;

public interface HistorieKlicRepository extends JpaRepository<HistorieKlic, String> {
    static final String sqlSelect = "select s from HistorieKlic s ";

    @Query(sqlSelect + "where s.klic.idKlic = :idKlic")
    List<HistorieKlic> findByIdKlic(String idKlic);

    @Query(sqlSelect + "where s.idHistorieKlic = :idHistorieKlic")
    HistorieKlic getDetail(String idHistorieKlic);

}
