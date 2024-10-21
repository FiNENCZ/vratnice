package cz.diamo.vratnice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;

public interface PovoleniVjezduVozidlaRepository extends JpaRepository<PovoleniVjezduVozidla, String> {
    static final String sqlSelect = "select s from PovoleniVjezduVozidla s "; 

    @Query(sqlSelect + "where s.idPovoleniVjezduVozidla = :idPovoleniVjezduVozidla")
    PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla);

    @Query(sqlSelect + "where s.stav = :stav")
    List<PovoleniVjezduVozidla> getByStav(String stav);

    @Query(sqlSelect + "join s.rzVozidla rz where UPPER(rz) = UPPER(:rzVozidla)")
    List<PovoleniVjezduVozidla> getByRzVozidla(String rzVozidla);

    @Query("SELECT s.datumVytvoreni FROM PovoleniVjezduVozidla s WHERE s.idPovoleniVjezduVozidla = :idPovoleniVjezduVozidla")
    Date getDatumVytvoreni(String idPovoleniVjezduVozidla);

}
