package cz.dp.vratnice.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.VjezdVozidla;

public interface VjezdVozidlaRepository extends JpaRepository<VjezdVozidla, String> {
    static final String sqlSelect = "select s from VjezdVozidla s ";

    @Query(sqlSelect + "where s.idVjezdVozidla = :idVjezdVozidla")
    VjezdVozidla getDetail(String idVjezdVozidla);

    @Query(sqlSelect + "where s.rzVozidla = :rzVozidla")
    List<VjezdVozidla> getByRzVozidla(String rzVozidla);

    @Query(sqlSelect + "where s.rzVozidla = :rzVozidla AND s.casPrijezdu BETWEEN :datumOd AND :datumDo")
    List<VjezdVozidla> findByRzVozidlaAndDatumOdBetween(String rzVozidla, ZonedDateTime datumOd, ZonedDateTime datumDo);
}
