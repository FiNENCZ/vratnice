package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevaUzivatelStav;

public interface NavstevaUzivatelStavRepository extends JpaRepository<NavstevaUzivatelStav, String> {
    static final String sqlSelect = "select s from NavstevaUzivatelStav s ";

    @Query(sqlSelect + "where s.idNavstevaUzivatelStav = :idNavstevaUzivatelStav")
    NavstevaOsoba getDetail(String idNavstevaUzivatelStav);
}
