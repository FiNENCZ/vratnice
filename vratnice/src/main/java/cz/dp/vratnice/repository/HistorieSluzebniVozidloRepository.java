package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.HistorieSluzebniVozidlo;
import cz.dp.vratnice.entity.SluzebniVozidlo;

import java.util.List;


public interface HistorieSluzebniVozidloRepository extends JpaRepository <HistorieSluzebniVozidlo, String> {
    static final String sqlSelect = "select s from HistorieSluzebniVozidlo s ";

    @Query(sqlSelect + "where s.sluzebniVozidlo = :sluzebniVozidlo")
    List<HistorieSluzebniVozidlo> findBySluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo);

    @Query(sqlSelect + "where s.idHistorieSluzebniAuto = :idHistorieSluzebniAuto")
    HistorieSluzebniVozidlo getDetail(String idHistorieSluzebniAuto);
}
