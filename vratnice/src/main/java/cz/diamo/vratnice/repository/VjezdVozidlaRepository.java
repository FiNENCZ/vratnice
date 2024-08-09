package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.VjezdVozidla;

public interface VjezdVozidlaRepository extends JpaRepository<VjezdVozidla, String> {
    static final String sqlSelect = "select s from VjezdVozidla s ";

    @Query(sqlSelect + "where s.idVjezdVozidla = :idVjezdVozidla")
    VjezdVozidla getDetail(String idVjezdVozidla);

    @Query(sqlSelect + "where s.rzVozidla = :rzVozidla")
    List<VjezdVozidla> getByRzVozidla(String rzVozidla);

    @Query(sqlSelect+ "WHERE s.aktivita = :aktivita AND (s.zmenuProvedl = 'kamery' OR s.zmenuProvedl IS NULL)")
    List<VjezdVozidla> getNevyporadaneVjezdy(Boolean aktivita);

}
