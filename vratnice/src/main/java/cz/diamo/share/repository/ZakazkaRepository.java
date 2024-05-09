package cz.diamo.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Zakazka;

/**
 * Zakázka
 */
public interface ZakazkaRepository extends JpaRepository<Zakazka, String> {

	static final String sqlSelect = "select s from Zakazka s left join fetch s.zavod zav ";

	/**
	 * Detail
	 * 
	 * @param idZakazka Identifikátor záznamu
	 * @return Detail záznamu
	 */
	@Query(sqlSelect + "where s.idZakazka = :idZakazka")
	Zakazka getDetail(String idZakazka);

	/**
	 * Detail
	 * 
	 * @param idZakazka Identifikátor záznamu
	 * @param idZavod   Identifikátor závodu
	 * @return Detail záznamu
	 */
	@Query(sqlSelect + "where s.idZakazka = :idZakazka and zav.idZavod = :idZavod")
	Zakazka getDetail(String idZakazka, String idZavod);

	/**
	 * Detail dle SAP ID
	 * 
	 * @param sapId        SAP ID
	 * @param idZavod Identifikátor závodu
	 * @return Detail záznamu
	 */
	@Query(sqlSelect + "where s.sapId = :sapId and zav.idZavod = :idZavod")
	Zakazka getDetailBySapId(String sapId, String idZavod);

	/**
	 * Detail dle SAP ID
	 * 
	 * @param sapId        SAP ID
	 * @param idZavod Identifikátor závodu
	 * @return Detail záznamu
	 */
	@Query(sqlSelect + "where s.sapId = :sapId and zav.idZavod in (:idsZavod) and s.aktivita = :aktivita")
	Zakazka getDetailBySapId(String sapId, List<String> idsZavod, Boolean aktivita);

	/**
	 * Detail dle SAP ID
	 * 
	 * @param sapId        SAP ID
	 * @param idZavod Identifikátor závodu
	 * @return Detail záznamu
	 */
	@Query(sqlSelect + "where s.sapId = :sapId and zav.idZavod = :idZavod and s.aktivita = :aktivita")
	Zakazka getDetailBySapId(String sapId, String idZavod, Boolean aktivita);
	
	/**
	 * Test zda existuje v závodu daná zakázka dle sap ID
	 * 
	 * @param sapId SAP ID
	 * @param idZavod Identifikátor závodu
	 * @param idZakazka Identifikátor směnovnice který se nepočítá
	 * @return > 0 pokud existuje
	 */
	@Query("select count(s) from Zakazka s where s.sapId = :sapId and s.zavod.idZavod = :idZavod and s.idZakazka != :idZakazka")
	Integer existsBySapId(String sapId, String idZavod, String idZakazka);
}
