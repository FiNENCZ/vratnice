package cz.diamo.vratnice.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.PovoleniVjezduVozidlaZmenaStavu;

public interface PovoleniVjezduVozidlaZmenaStavuRepository extends JpaRepository<PovoleniVjezduVozidlaZmenaStavu, BigInteger> {
        static final String sqlSelect = "select s from PovoleniVjezduVozidlaZmenaStavu s left join s.povoleniVjezduVozidla povoleniVjezduVozidla left join fetch s.stavNovy stavNovy left join s.stavPuvodni stavPuvodni left join fetch s.uzivatel uzivatel ";

        @Query(sqlSelect + "where povoleniVjezduVozidla.idPovoleniVjezduVozidla = :idPovoleniVjezduVozidla  order by s.cas DESC")
        List<PovoleniVjezduVozidlaZmenaStavu> getList(String idPovoleniVjezduVozidla);

        @Query(sqlSelect + "where povoleniVjezduVozidla.idPovoleniVjezduVozidla = :idPovoleniVjezduVozidla order by s.cas DESC limit 1")
        PovoleniVjezduVozidlaZmenaStavu findLatestById(String idPovoleniVjezduVozidla);

}
