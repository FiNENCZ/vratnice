package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.UzivatelVsechnyVratnice;

public interface UzivatelVsechnyVratniceRepository extends JpaRepository<UzivatelVsechnyVratnice, String> {
    static final String sqlSelect = "select s from UzivatelVsechnyVratnice s ";

    @Query(sqlSelect + "where s.idUzivatel = :idUzivatel")
    UzivatelVsechnyVratnice getDetail(String idUzivatel);

}
