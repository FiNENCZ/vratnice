package cz.diamo.share.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.ExterniUzivatel;

public interface ExterniUzivatelRepository extends JpaRepository<ExterniUzivatel, String> {

    static final String sqlSelect = "select s from ExterniUzivatel s ";

    @Query(sqlSelect + "where s.idExterniUzivatel = :idExterniUzivatel")
    ExterniUzivatel getDetail(String idExterniUzivatel);

    @Query(sqlSelect + "where upper(s.username) = upper(:username)")
    ExterniUzivatel getDetailByUsername(String username);

    @Query("select count(s) from ExterniUzivatel s where upper(s.username) = upper(:username) and s.idExterniUzivatel != :idExterniUzivatel")
    Integer existsByUsername(String username, String idExterniUzivatel);

    @Modifying
    @Query(value = "update " + Constants.SCHEMA  + ".externi_uzivatel set aktivita = :aktivita, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_externi_uzivatel = :idExterniUzivatel", nativeQuery = true)
    void zmenaAktivity(String idExterniUzivatel, Boolean aktivita, Timestamp casZmeny,
            String zmenuProv);

    @Query(value = "select nazev from " + Constants.SCHEMA  + ".externi_uzivatel where username = :userName", nativeQuery = true)
    String getZmenuProvTxt(String userName);
}
