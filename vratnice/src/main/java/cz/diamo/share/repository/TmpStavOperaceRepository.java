package cz.diamo.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.TmpStavOperace;

public interface TmpStavOperaceRepository extends JpaRepository<TmpStavOperace, TmpStavOperace> {

	static final String sqlSelect = "select t from TmpStavOperace t ";

	@Query(sqlSelect + "where t.idUzivatel = :idUzivatel and t.idOperace = :idOperace")
	TmpStavOperace getDetail(String idUzivatel, String idOperace);

	@Modifying
	@Query(value = "delete from " + Constants.SCHEMA + ".tmp_stav_operace where id_uzivatel = :idUzivatel and id_operace = :idOperace", nativeQuery = true)
	void deleteByPk(String idUzivatel, String idOperace);
}
