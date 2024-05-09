package cz.diamo.share.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Opravneni;

public interface OpravneniRepository extends JpaRepository<Opravneni, String> {
        static final String sqlSelect = "select s from Opravneni s left join fetch s.opravneniTypPristupu typ ";

        @Query(sqlSelect + "where s.idOpravneni = :idOpravneni and (:idZavod is null or s.idOpravneni in (select v.idOpravneni from OpravneniZavod v where v.idZavod = :idZavod))")
        Opravneni getDetail(String idOpravneni, String idZavod);

        @Query(sqlSelect + "where upper(s.kod) = :kod and (:idZavod is null or s.idOpravneni in (select v.idOpravneni from OpravneniZavod v where v.idZavod = :idZavod)) and (:aktivita is null or s.aktivita = :aktivita)")
        Opravneni getDetailByKod(String kod, String idZavod, Boolean aktivita);

        @Query(sqlSelect + "where (:idZavod is null or s.idOpravneni in (select v.idOpravneni from OpravneniZavod v where v.idZavod = :idZavod)) and (:aktivita is null or s.aktivita = :aktivita) order by s.nazev ASC")
        List<Opravneni> getList(String idZavod, Boolean aktivita);

        @Query("select count(s) from Opravneni s where s.kod = :kod and s.idOpravneni != :idOpravneni")
        Integer exists(String kod, String idOpravneni);

        @Modifying
        @Query(value = "update " + Constants.SCHEMA
                        + ".opravneni set aktivita = :aktivita, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_opravneni = :idOpravneni", nativeQuery = true)
        void zmenaAktivity(String idOpravneni, Boolean aktivita, Timestamp casZmeny, String zmenuProv);

        @Query(value = "select distinct " + "uzivatel_nadrizeny.id_uzivatel " + "from " + Constants.SCHEMA + ".uzivatel uzivatel_podrizeny " + "join "
                        + Constants.SCHEMA
                        + ".uzivatel uzivatel_nadrizeny on (((uzivatel_nadrizeny.id_zavod = uzivatel_podrizeny.id_zavod and :idZavodVozidlo is null) or (uzivatel_nadrizeny.id_zavod = :idZavodVozidlo)) and uzivatel_nadrizeny.aktivita = true) "
                        + "join " + Constants.SCHEMA
                        + ".uzivatel_opravneni uzivatel_opravneni on (uzivatel_opravneni.id_uzivatel = uzivatel_nadrizeny.id_uzivatel) " + "join "
                        + Constants.SCHEMA
                        + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.id_opravneni_typ_pristupu = 2) "
                        + "join " + Constants.SCHEMA
                        + ".opravneni_role on (opravneni_role.id_opravneni = opravneni.id_opravneni and opravneni_role.authority in :role) " + "where "
                        + "uzivatel_podrizeny.id_uzivatel = :idUzivatelPodrizeny " + "union " + "select distinct " + "uzivatel_nadrizeny.id_uzivatel " + "from "
                        + Constants.SCHEMA + ".uzivatel uzivatel_podrizeny " + "join " + Constants.SCHEMA
                        // + ".uzivatel uzivatel_nadrizeny on (((uzivatel_nadrizeny.id_zavod =
                        // uzivatel_podrizeny.id_zavod and :idZavodVozidlo is null) or
                        // (uzivatel_nadrizeny.id_zavod = :idZavodVozidlo)) and
                        // uzivatel_nadrizeny.aktivita = true) "
                        + ".uzivatel uzivatel_nadrizeny on (uzivatel_nadrizeny.aktivita = true) "// zrušeno omezení na závod 2024-01-19 - kvůli rediteli
                        + "join " + Constants.SCHEMA
                        + ".uzivatel_opravneni uzivatel_opravneni on (uzivatel_opravneni.id_uzivatel = uzivatel_nadrizeny.id_uzivatel) " + "join "
                        + Constants.SCHEMA
                        + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.id_opravneni_typ_pristupu = 3) "
                        + "join " + Constants.SCHEMA
                        + ".opravneni_role on (opravneni_role.id_opravneni = opravneni.id_opravneni and opravneni_role.authority in :role) " + "join "
                        + Constants.SCHEMA
                        + ".opravneni_pracovni_pozice opravneni_pracovni_pozice on (opravneni_pracovni_pozice.id_opravneni = opravneni.id_opravneni and opravneni_pracovni_pozice.id_pracovni_pozice = uzivatel_podrizeny.id_pracovni_pozice) "
                        + "where " + "uzivatel_podrizeny.id_uzivatel = :idUzivatelPodrizeny " + "union " + "select distinct "
                        + "uzivatel_nadrizeny.id_uzivatel " + "from " + Constants.SCHEMA + ".uzivatel uzivatel_podrizeny " + "join " + Constants.SCHEMA
                        // + ".uzivatel uzivatel_nadrizeny on (((uzivatel_nadrizeny.id_zavod =
                        // uzivatel_podrizeny.id_zavod and :idZavodVozidlo is null) or
                        // (uzivatel_nadrizeny.id_zavod = :idZavodVozidlo)) and
                        // uzivatel_nadrizeny.aktivita = true) "
                        + ".uzivatel uzivatel_nadrizeny on (uzivatel_nadrizeny.aktivita = true) "// zrušeno omezení na závod 2024-01-19 - kvůli rediteli
                        + "join " + Constants.SCHEMA
                        + ".uzivatel_opravneni uzivatel_opravneni on (uzivatel_opravneni.id_uzivatel = uzivatel_nadrizeny.id_uzivatel) " + "join "
                        + Constants.SCHEMA
                        + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.id_opravneni_typ_pristupu = 4) "
                        + "join " + Constants.SCHEMA
                        + ".opravneni_role on (opravneni_role.id_opravneni = opravneni.id_opravneni and opravneni_role.authority in :role) " + "join "
                        + Constants.SCHEMA
                        + ".pracovni_pozice_podrizene on (pracovni_pozice_podrizene.id_pracovni_pozice = uzivatel_nadrizeny.id_pracovni_pozice and pracovni_pozice_podrizene.id_pracovni_pozice_podrizeny = uzivatel_podrizeny.id_pracovni_pozice and pracovni_pozice_podrizene.aktivita = true and pracovni_pozice_podrizene.primy_podrizeny = true) "
                        + "where " + "uzivatel_podrizeny.id_uzivatel = :idUzivatelPodrizeny " + "union " + "select distinct "
                        + "uzivatel_nadrizeny.id_uzivatel " + "from " + Constants.SCHEMA + ".uzivatel uzivatel_podrizeny " + "join " + Constants.SCHEMA
                        // + ".uzivatel uzivatel_nadrizeny on (((uzivatel_nadrizeny.id_zavod =
                        // uzivatel_podrizeny.id_zavod and :idZavodVozidlo is null) or
                        // (uzivatel_nadrizeny.id_zavod = :idZavodVozidlo)) and
                        // uzivatel_nadrizeny.aktivita = true) "
                        + ".uzivatel uzivatel_nadrizeny on (uzivatel_nadrizeny.aktivita = true) "// zrušeno omezení na závod 2024-01-19 - kvůli rediteli
                        + "join " + Constants.SCHEMA
                        + ".uzivatel_opravneni uzivatel_opravneni on (uzivatel_opravneni.id_uzivatel = uzivatel_nadrizeny.id_uzivatel) " + "join "
                        + Constants.SCHEMA
                        + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.id_opravneni_typ_pristupu = 5) "
                        + "join " + Constants.SCHEMA
                        + ".opravneni_role on (opravneni_role.id_opravneni = opravneni.id_opravneni and opravneni_role.authority in :role) " + "join "
                        + Constants.SCHEMA
                        + ".pracovni_pozice_podrizene on (pracovni_pozice_podrizene.id_pracovni_pozice = uzivatel_nadrizeny.id_pracovni_pozice and pracovni_pozice_podrizene.id_pracovni_pozice_podrizeny = uzivatel_podrizeny.id_pracovni_pozice and pracovni_pozice_podrizene.aktivita = true) "
                        + "where " + "uzivatel_podrizeny.id_uzivatel = :idUzivatelPodrizeny", nativeQuery = true)
        List<String> getListNadrizeni(String idUzivatelPodrizeny, List<String> role, String idZavodVozidlo);

}
