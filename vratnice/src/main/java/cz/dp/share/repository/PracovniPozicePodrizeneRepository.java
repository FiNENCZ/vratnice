package cz.dp.share.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.PracovniPozicePodrizene;

public interface PracovniPozicePodrizeneRepository extends JpaRepository<PracovniPozicePodrizene, String> {

	static final String sqlSelect = "select s from PracovniPozicePodrizene s left join fetch s.pracovniPozice pozice left join fetch s.pracovniPozicePodrizeny pozicePodrizeny ";

	@Query(sqlSelect + "where s.idPracovniPozicePodrizene = :idPracovniPozicePodrizene")
	PracovniPozicePodrizene getDetail(String idPracovniPozicePodrizene);

	@Query(sqlSelect + "where pozice.idPracovniPozice = :idPracovniPozice and pozicePodrizeny.idPracovniPozice = :idPracovniPozicePodrizeny")
	PracovniPozicePodrizene getDetail(String idPracovniPozice, String idPracovniPozicePodrizeny);

	@Query("select count(s) from PracovniPozicePodrizene s where s.pracovniPozice.idPracovniPozice = :idPracovniPozice and s.pracovniPozicePodrizeny.idPracovniPozice = :idPracovniPozicePodrizeny and s.idPracovniPozicePodrizene != :idPracovniPozicePodrizene")
	Integer exists(String idPracovniPozice, String idPracovniPozicePodrizeny, String idPracovniPozicePodrizene);

	@Query(value = "select s.id_pracovni_pozice_podrizene from " + Constants.SCHEMA + ".pracovni_pozice_podrizene s, " + Constants.SCHEMA
			+ ".pracovni_pozice nadrizeny where nadrizeny.id_pracovni_pozice = s.id_pracovni_pozice and s.aktivita = true", nativeQuery = true)
	List<String> getListIdAktivni();

	@Modifying
	@Query(value = "update " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene set aktivita = false, cas_zmn = :casZmn, zmenu_provedl = :zmenuProvedl WHERE id_pracovni_pozice_podrizene = :idPracovniPozicePodrizene and aktivita = true", nativeQuery = true)
	void odstranit(String idPracovniPozicePodrizene, Timestamp casZmn, String zmenuProvedl);

	@Query(value = "select count(pracovni_pozice_podrizene.id_pracovni_pozice_podrizene) from " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene pracovni_pozice_podrizene, " + Constants.SCHEMA + ".uzivatel uzivatel, " + Constants.SCHEMA
			+ ".uzivatel podrizeny where pracovni_pozice_podrizene.id_pracovni_pozice_podrizeny = podrizeny.id_pracovni_pozice and pracovni_pozice_podrizene.id_pracovni_pozice = uzivatel.id_pracovni_pozice and uzivatel.id_uzivatel = :idUzivatel and podrizeny.id_uzivatel = :idUzivatelPodrizeny and pracovni_pozice_podrizene.aktivita = true", nativeQuery = true)
	Integer existsPodrizenost(String idUzivatel, String idUzivatelPodrizeny);

	@Query(value = "select count(pracovni_pozice_podrizene.id_pracovni_pozice_podrizene) from " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene pracovni_pozice_podrizene, " + Constants.SCHEMA + ".uzivatel uzivatel, " + Constants.SCHEMA
			+ ".uzivatel podrizeny where pracovni_pozice_podrizene.id_pracovni_pozice_podrizeny = podrizeny.id_pracovni_pozice and pracovni_pozice_podrizene.id_pracovni_pozice = uzivatel.id_pracovni_pozice and uzivatel.id_uzivatel = :idUzivatel and podrizeny.id_uzivatel = :idUzivatelPodrizeny and pracovni_pozice_podrizene.aktivita = true and pracovni_pozice_podrizene.primy_podrizeny = true", nativeQuery = true)
	Integer existsPodrizenostPrima(String idUzivatel, String idUzivatelPodrizeny);

	@Query(value = "select" + " podrizeny.id_uzivatel" + " from" + " " + Constants.SCHEMA + ".pracovni_pozice_podrizene podrizene" + " join " + Constants.SCHEMA
			+ ".uzivatel nadrizeny on" + " (nadrizeny.id_pracovni_pozice = podrizene.id_pracovni_pozice" + " and nadrizeny.aktivita = true"
			+ " and nadrizeny.ukonceno = false" + " and nadrizeny.datum_od <= :datum" + " and (nadrizeny.datum_do is null" + " or nadrizeny.datum_do >= :datum)"
			+ " and nadrizeny.id_uzivatel = :idUzivatel)" + " join " + Constants.SCHEMA + ".uzivatel podrizeny on"
			+ " (podrizeny.id_pracovni_pozice = podrizene.id_pracovni_pozice_podrizeny" + " and podrizeny.aktivita = true" + " and podrizeny.ukonceno = false"
			+ " and podrizeny.datum_od <= :datum" + " and (podrizeny.datum_do is null" + " or podrizeny.datum_do >= :datum))" + " where"
			+ " podrizene.aktivita = true" + " and podrizene.primy_podrizeny = true", nativeQuery = true)
	List<String> getListPrimiPodrizeni(String idUzivatel, Date datum);

	@Query(value = "select" + " podrizeny.id_uzivatel" + " from" + " " + Constants.SCHEMA + ".pracovni_pozice_podrizene podrizene" + " join " + Constants.SCHEMA
			+ ".uzivatel nadrizeny on" + " (nadrizeny.id_pracovni_pozice = podrizene.id_pracovni_pozice" + " and nadrizeny.aktivita = true"
			+ " and nadrizeny.ukonceno = false" + " and nadrizeny.datum_od <= :datum" + " and (nadrizeny.datum_do is null" + " or nadrizeny.datum_do >= :datum)"
			+ " and nadrizeny.id_uzivatel = :idUzivatel)" 
			+ " join " + Constants.SCHEMA + ".pracovni_pozice pp_podrizeny on"
			+" (pp_podrizeny.id_pracovni_pozice = podrizene.id_pracovni_pozice_podrizeny and length(pp_podrizeny.zkratka) = 5 and left (pp_podrizeny.zkratka,1) = '1')"
			+ " join " + Constants.SCHEMA + ".uzivatel podrizeny on"
			+ " (podrizeny.id_pracovni_pozice = podrizene.id_pracovni_pozice_podrizeny" + " and podrizeny.aktivita = true" + " and podrizeny.ukonceno = false"
			+ " and podrizeny.datum_od <= :datum" + " and (podrizeny.datum_do is null" + " or podrizeny.datum_do >= :datum))" + " where"
			+ " podrizene.aktivita = true" + " and podrizene.primy_podrizeny = true", nativeQuery = true)
	List<String> getListPrimiPodrizeniReditele(String idUzivatel, Date datum);
}
