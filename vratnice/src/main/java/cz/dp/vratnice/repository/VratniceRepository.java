package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.Vratnice;

public interface VratniceRepository extends JpaRepository<Vratnice, String> {
    static final String sqlSelect = "select s from Vratnice s ";

    @Query(sqlSelect + "where s.idVratnice = :idVratnice")
    Vratnice getDetail(String idVratnice);

}
