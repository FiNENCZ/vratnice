package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.entity.UzivatelVratnice;

public interface UzivatelVratniceRepository extends JpaRepository<UzivatelVratnice, String> {
    static final String sqlSelect = "select s from UzivatelVratnice s ";

    @Query(sqlSelect + "where s.idUzivatelVratnice = :idUzivatelVratnice")
    UzivatelVratnice getDetail(String idUzivatelVratnice);

    @Query(sqlSelect + "where s.uzivatel = :uzivatel")
    UzivatelVratnice getByUzivatel(Uzivatel uzivatel);

    @Query("select case when count(u) > 0 then true else false end from UzivatelVratnice u where u.uzivatel.idUzivatel = :idUzivatel")
    boolean existsByIdUzivatel(String idUzivatel);

}
