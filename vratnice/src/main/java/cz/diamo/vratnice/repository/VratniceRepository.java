package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Vratnice;

public interface VratniceRepository extends JpaRepository<Vratnice, String> {
    static final String sqlSelect = "select s from Vratnice s ";

    @Query(sqlSelect + "where s.idVratnice = :idVratnice")
    Vratnice getDetail(String idVratnice);

}
