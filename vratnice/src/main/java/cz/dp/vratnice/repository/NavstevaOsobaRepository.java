package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.NavstevaOsoba;


public interface NavstevaOsobaRepository extends JpaRepository<NavstevaOsoba, String> {
    static final String sqlSelect = "select s from NavstevaOsoba s ";

    @Query(sqlSelect + "where s.idNavstevaOsoba = :idNavstevaOsoba")
    NavstevaOsoba getDetail(String idNavstevaOsoba);

    @Query(sqlSelect + "where s.cisloOp = :cisloOp")
    NavstevaOsoba geByCisloOp(String cisloOp);

    Boolean existsByCisloOp(String cisloOp);

}
