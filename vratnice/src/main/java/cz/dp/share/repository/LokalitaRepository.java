package cz.dp.share.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Lokalita;

public interface LokalitaRepository extends JpaRepository<Lokalita, String> {
    static final String sqlSelect = "select s from Lokalita s left join fetch s.zavod zav ";

    @Query(sqlSelect + "where s.idLokalita = :idLokalita")
    Lokalita getDetail(String idLokalita);

	@Query(sqlSelect + "where s.kod = :kod")
	Lokalita getDetailByKod(String kod);

	@Query("select count(s) from Lokalita s where s.kod = :kod and s.idLokalita != :idLokalita")
	Integer existsByKod(String kod, String idLokalita);

	@Query(sqlSelect + "where zav.idZavod = :idZavod and s.aktivita = :aktivita order by s.nazev")
	List<Lokalita> getList(String idZavod, Boolean aktivita);

    /**
	 * Změna veřejné
	 * 
	 * @param idLokalita
	 * @param verejne
	 * @param casZmeny
	 * @param zmenuProv
	 */
	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".lokalita set verejne = :verejne, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_lokalita = :idLokalita", nativeQuery = true)
	void zmenaVerejne(String idLokalita, Boolean verejne, Timestamp casZmeny, String zmenuProv);

    /**
	 * Změna aktivity
	 * 
	 * @param idLokalita
	 * @param aktivita
	 * @param casZmeny
	 * @param zmenuProv
	 */
	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".lokalita set aktivita = :aktivita, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_lokalita = :idLokalita", nativeQuery = true)
	void zmenaAktivity(String idLokalita, Boolean aktivita, Timestamp casZmeny, String zmenuProv);

    /**
	 * Změna aktivity
	 * 
	 * @param idsLokalita
	 * @param aktivita
	 * @param casZmeny
	 * @param zmenuProv
	 */
	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".lokalita set aktivita = :aktivita, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_lokalita not in (:idsLokalita)", nativeQuery = true)
	void zmenaAktivityHromadneNotIn(List<String> idsLokalita, Boolean aktivita, Timestamp casZmeny, String zmenuProv);
}
