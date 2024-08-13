package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.VyjezdVozidla;

public interface VyjezdVozidlaRepository extends JpaRepository<VyjezdVozidla, String> {
    static final String sqlSelect = "select s from VyjezdVozidla s ";

    @Query(sqlSelect + "where s.idVyjezdVozidla = :idVyjezdVozidla")
    VyjezdVozidla getDetail(String idVyjezdVozidla);

    @Query(sqlSelect + "where s.rzVozidla = :rzVozidla")
    List<VyjezdVozidla> getByRzVozidla(String rzVozidla);

}
