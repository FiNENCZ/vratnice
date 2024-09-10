package cz.diamo.vratnice.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.vratnice.entity.Poschodi;

public interface PoschodiRepository extends JpaRepository<Poschodi, String> {
    static final String sqlSelect = "select s from Poschodi s left join fetch s.budova bud ";

    @Query(sqlSelect + "where s.idPoschodi = :idPoschodi")
    Poschodi getDetail(String idPoschodi);

	@Query(sqlSelect + "where bud.idBudova = :idBudova and s.aktivita = :aktivita order by s.nazev")
	List<Poschodi> getList(String idBudova, Boolean aktivita);

    /**
	 * Změna aktivity
	 * 
	 * @param idPoschodi
	 * @param aktivita
	 * @param casZmeny
	 * @param zmenuProv
	 */
	@Modifying
	@Query(value = "update " + Constants.SCHEMA  + ".poschodi set aktivita = :aktivita, cas_zmn = :casZmeny, zmenu_provedl = :zmenuProv where id_poschodi = :idPoschodi", nativeQuery = true)
	void zmenaAktivity(String idPoschodi, Boolean aktivita, Timestamp casZmeny, String zmenuProv);
}
