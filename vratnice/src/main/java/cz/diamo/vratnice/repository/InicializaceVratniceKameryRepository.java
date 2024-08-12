package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.InicializaceVratniceKamery;

public interface InicializaceVratniceKameryRepository extends JpaRepository<InicializaceVratniceKamery, String> {

    static final String sqlSelect = "select s from InicializaceVratniceKamery s ";

    @Query(sqlSelect + "where s.ipAdresa = :ipAdresa")
    InicializaceVratniceKamery getByIpAdresa(String ipAdresa);

    Boolean existsByIpAdresa(String ipAdresa);


}
