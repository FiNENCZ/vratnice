package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.Zavod;

public interface ZavodRepository extends JpaRepository<Zavod, String> {
	static final String sqlSelect = "select s from Zavod s ";

	@Query(sqlSelect + "where s.idZavod = :idZavod")
	Zavod getDetail(String idZavod);

	@Query(sqlSelect + "where s.sapId = :sapId")
	Zavod getDetailBySapId(String sapId);

	@Query(sqlSelect + "where (:aktivita is null or s.aktivita = :aktivita) order by s.nazev asc")
	List<Zavod> getList(Boolean aktivita);

	@Query("select count(s) from Zavod s where s.sapId = :sapId and s.idZavod != :idZavod")
	Integer existsBySapId(String sapId, String idZavod);
}
