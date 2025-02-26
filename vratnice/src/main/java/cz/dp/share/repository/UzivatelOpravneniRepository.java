package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.Opravneni;
import cz.dp.share.entity.Role;
import cz.dp.share.entity.UzivatelOpravneni;

public interface UzivatelOpravneniRepository extends JpaRepository<UzivatelOpravneni, UzivatelOpravneni> {

    @Query("select o from Opravneni o where o.idOpravneni in (select v.idOpravneni from UzivatelOpravneni v where v.idUzivatel = :idUzivatel) and (:aktivita is null or o.aktivita = :aktivita) order by o.nazev ASC")
    List<Opravneni> listOpravneni(String idUzivatel, Boolean aktivita);

    @Query("select distinct r from Role r where r.authority in (select v.authority from OpravneniRole v, Opravneni opravneni where v.idOpravneni = opravneni.idOpravneni and (:aktivita is null or opravneni.aktivita = :aktivita) and v.idOpravneni in (select v.idOpravneni from UzivatelOpravneni v where v.idUzivatel = :idUzivatel))  order by r.authority ASC")
    List<Role> listRole(String idUzivatel, Boolean aktivita);

    @Query("select count(v) from UzivatelOpravneni v where v.idUzivatel = :idUzivatel and v.idOpravneni = :idOpravneni")
    Integer exists(String idUzivatel, String idOpravneni);

}
