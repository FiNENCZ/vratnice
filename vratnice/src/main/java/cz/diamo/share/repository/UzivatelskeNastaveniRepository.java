package cz.diamo.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.UzivatelskeNastaveni;

public interface UzivatelskeNastaveniRepository extends JpaRepository<UzivatelskeNastaveni, String> {

    @Query("select s from UzivatelskeNastaveni s left join fetch s.uzivatel u where u.idUzivatel = :idUzivatel and s.klic = :klic")
    UzivatelskeNastaveni getDetail(String idUzivatel, String klic);

    @Modifying
    @Query("delete from UzivatelskeNastaveni s where s.uzivatel.idUzivatel = :idUzivatel and s.klic = :klic")
    void delete(String idUzivatel, String klic);

    @Modifying
    @Query("delete from UzivatelskeNastaveni s where s.uzivatel.idUzivatel = ?1")
    void deleteAll(String idUzivatel);

}
