package cz.diamo.share.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.KmenovaData;

public interface KmenovaDataRepository extends JpaRepository<KmenovaData, String> {

	static final String sqlSelect = "select s from KmenovaData s left join fetch s.zavod zav ";

	@Query(sqlSelect + "where s.idKmenovaData = :idKmenovaData")
	KmenovaData getDetail(String idKmenovaData);

	@Query(sqlSelect + "where s.idKmenovaData = :idKmenovaData and zav.idZavod = :idZavod")
	KmenovaData getDetail(String idKmenovaData, String idZavod);

	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".kmenova_data set zpracovano = true, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_kmenova_data = :idKmenovaData", nativeQuery = true)
	void oznacitZaZpracovane(String idKmenovaData, Timestamp casZmeny,
			String zmenuProv);

	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".kmenova_data set chyba = :chyba, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_kmenova_data = :idKmenovaData", nativeQuery = true)
	void zapsatChybu(String idKmenovaData, String chyba, Timestamp casZmeny, String zmenuProv);
}
