package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.UzivatelVratnice;

public interface UzivatelVratniceRepository extends JpaRepository<UzivatelVratnice, String> {
    static final String sqlSelect = "select s from UzivatelVratnice s ";

    @Query(sqlSelect + "where s.idUzivatelVratnice = :idUzivatelVratnice")
    UzivatelVratnice getDetail(String idUzivatelVratnice);

}
