package cz.dp.share.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Budova;

public interface BudovaRepository extends JpaRepository<Budova, String> {
    static final String sqlSelect = "select s from Budova s left join fetch s.lokalita lok ";

    @Query(sqlSelect + "where s.idBudova = :idBudova")
    Budova getDetail(String idBudova);

	@Query(sqlSelect + "where s.idExterni = :idExterni")
	Budova getDetailByIdExterni(String idExterni);

	@Query("select count(s) from Budova s where s.idExterni = :idExterni and s.idBudova != :idBudova")
	Integer existsByIdExterni(String idExterni, String idBudova);

	@Query(sqlSelect + "where lok.idLokalita = :idLokalita and s.aktivita = :aktivita order by s.nazev")
	List<Budova> getList(String idLokalita, Boolean aktivita);

    /**
	 * Změna aktivity
	 * 
	 * @param idBudova
	 * @param aktivita
	 * @param casZmeny
	 * @param zmenuProv
	 */
	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".budova set aktivita = :aktivita, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_budova = :idBudova", nativeQuery = true)
	void zmenaAktivity(String idBudova, Boolean aktivita, Timestamp casZmeny, String zmenuProv);
}
