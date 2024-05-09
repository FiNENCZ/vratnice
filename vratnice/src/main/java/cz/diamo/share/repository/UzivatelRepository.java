package cz.diamo.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Uzivatel;

public interface UzivatelRepository extends JpaRepository<Uzivatel, String> {

    static final String sqlSelect = "select s from Uzivatel s left join fetch s.zavod zav left join fetch s.zakazka zak left join fetch s.zavodVyber zavVyb left join fetch s.pracovniPozice pozice ";

    @Query(sqlSelect + "where s.idUzivatel = :idUzivatel")
    Uzivatel getDetail(String idUzivatel);
   
    @Query(sqlSelect + "where s.sapId = :sapId")
    Uzivatel getDetailBySapId(String sapId);

    @Query("select s.sapId from Uzivatel s where s.idUzivatel = :idUzivatel")
    String getSapId(String idUzivatel);

    @Query("select count(s) from Uzivatel s where s.sapId = :sapId and s.idUzivatel != :idUzivatel")
	Integer existsBySapId(String sapId, String idUzivatel);

}
