package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.NajemnikNavstevnickaKarta;


public interface NajemnikNavstevnickaKartaRepository extends JpaRepository<NajemnikNavstevnickaKarta, String> {
    static final String sqlSelect = "select s from NajemnikNavstevnickaKarta s ";

    @Query(sqlSelect + "where s.idNajemnikNavstevnickaKarta = :idNajemnikNavstevnickaKarta")
    NajemnikNavstevnickaKarta getDetail(String idNajemnikNavstevnickaKarta);

    @Query(sqlSelect + "where s.cisloOp = :cisloOp")
    NajemnikNavstevnickaKarta getByCisloOp(String cisloOp);

    Boolean existsByCisloOp(String cisloOp);

}
