package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.Ridic;

public interface RidicRepository extends JpaRepository<Ridic, String> {
    static final String sqlSelect = "select s from Ridic s ";

    @Query(sqlSelect + "where s.idRidic = :idRidic")
    Ridic getDetail(String idRidic);

    @Query(sqlSelect + "where s.cisloOp = :cisloOp")
    Ridic getRidicByCisloOp(String cisloOp);

    Boolean existsByCisloOp(String cisloOp);

}
