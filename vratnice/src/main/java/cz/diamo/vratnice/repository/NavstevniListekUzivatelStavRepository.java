package cz.diamo.vratnice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;


public interface NavstevniListekUzivatelStavRepository extends JpaRepository<NavstevniListekUzivatelStav, String> {
    static final String sqlSelect = "select s from NavstevniListekUzivatelStav s ";

    @Query(sqlSelect + "where s.idNavstevniListekUzivatelStav = :idNavstevniListekUzivatelStav")
    NavstevniListekUzivatelStav getDetail(String idNavstevniListekUzivatelStav);

    @Query(sqlSelect + "where s.navstevniListek.idNavstevniListek = :idNavstevniListek")
    List<NavstevniListekUzivatelStav> getByNavstevniListek(String idNavstevniListek);

    @Query(sqlSelect + "where s.navstevniListek.idNavstevniListek = :idNavstevniListek AND s.uzivatel.idUzivatel = :idUzivatel")
    NavstevniListekUzivatelStav getByNavstevniListekAndUzivatel(String idNavstevniListek, String idUzivatel);
}
