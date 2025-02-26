package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.InicializaceVratniceKamery;

public interface InicializaceVratniceKameryRepository extends JpaRepository<InicializaceVratniceKamery, String> {

    static final String sqlSelect = "select s from InicializaceVratniceKamery s ";

    @Query(sqlSelect + "where s.ipAdresa = :ipAdresa")
    InicializaceVratniceKamery getByIpAdresa(String ipAdresa);


    @Query(sqlSelect + "where s.idInicializaceVratniceKamery = :idInicializaceVratniceKamery")
    InicializaceVratniceKamery getDetail(String idInicializaceVratniceKamery);

}
