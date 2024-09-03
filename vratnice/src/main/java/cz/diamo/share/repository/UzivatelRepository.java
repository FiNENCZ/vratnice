package cz.diamo.share.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Uzivatel;

public interface UzivatelRepository extends JpaRepository<Uzivatel, String> {

    static final String sqlSelect = "select s from Uzivatel s left join fetch s.zavod zav left join fetch s.zakazka zak left join fetch s.zavodVyber zavVyb left join fetch s.pracovniPozice pozice ";

    @Query(sqlSelect + "where s.idUzivatel = :idUzivatel")
    Uzivatel getDetail(String idUzivatel);

    @Query(sqlSelect + "where s.sapId = :sapId")
    Uzivatel getDetailBySapId(String sapId);

    @Query(sqlSelect
            + "where s.aktivita = true and (upper(s.cip1) = upper(:rfid) or upper(s.cip2) = upper(:rfid)) order by s.idUzivatel DESC limit 1")
    Uzivatel getDetailByRfid(String rfid);

    @Query("select s.sapId from Uzivatel s where s.idUzivatel = :idUzivatel")
    String getSapId(String idUzivatel);

    @Query("select count(s) from Uzivatel s where s.sapId = :sapId and s.idUzivatel != :idUzivatel")
    Integer existsBySapId(String sapId, String idUzivatel);

    @Query("select count(s)+1 from Uzivatel s where s.externi = true")
    Integer porCisloExt();

    @Modifying
    @Query("update Uzivatel u set u.cip1 = null, u.casZmn = :casZmn, u.zmenuProvedl = :zmenuProvedl where (:idUzivatel is null or u.idUzivatel != :idUzivatel) and (:cip1 is not null and u.cip1 = :cip1 or :cip2 is not null and u.cip1=:cip2)")
    void resetCip1(String cip1, String cip2, String idUzivatel, Timestamp casZmn, String zmenuProvedl);

    @Modifying
    @Query("update Uzivatel u set u.cip2 = null, u.casZmn = :casZmn, u.zmenuProvedl = :zmenuProvedl where (:idUzivatel is null or u.idUzivatel != :idUzivatel) and (:cip1 is not null and u.cip2 = :cip1 or :cip2 is not null and u.cip2=:cip2)")
    void resetCip2(String cip1, String cip2, String idUzivatel, Timestamp casZmn, String zmenuProvedl);
}
