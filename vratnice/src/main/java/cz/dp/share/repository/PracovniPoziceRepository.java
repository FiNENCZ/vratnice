package cz.dp.share.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.PracovniPozice;

public interface PracovniPoziceRepository extends JpaRepository<PracovniPozice, String> {
    static final String sqlSelect = "select s from PracovniPozice s ";

    @Query(sqlSelect + "where s.idPracovniPozice = :idPracovniPozice")
    PracovniPozice getDetail(String idPracovniPozice);

    @Query(sqlSelect + "where s.sapId = :sapId")
    PracovniPozice getDetailBySapId(String sapId);

    @Query("select count(s) from PracovniPozice s where s.sapId = :sapId and s.idPracovniPozice != :idPracovniPozice")
    Integer existsBySapId(String sapId, String idPracovniPozice);

    @Query(value = "select s.id_pracovni_pozice from " + Constants.SCHEMA
            + ".pracovni_pozice s where s.aktivita = true", nativeQuery = true)
    List<String> getListIdAktivni();

    @Query(value = "select s.sap_id from " + Constants.SCHEMA
            + ".pracovni_pozice s where s.aktivita = true and s.sap_id_dohodar = :sapIdDohodar order by s.nazev asc limit 1", nativeQuery = true)
    String getSapIdProDohodare(String sapIdDohodar);

    @Modifying
    @Query(value = "update " + Constants.SCHEMA
            + ".pracovni_pozice set aktivita = false, cas_zmn = :casZmn, zmenu_provedl = :zmenuProvedl WHERE id_pracovni_pozice = :idPracovniPozice and aktivita = true", nativeQuery = true)
    void odstranit(String idPracovniPozice, Timestamp casZmn, String zmenuProvedl);

}
