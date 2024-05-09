package cz.diamo.share.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Zastup;

public interface ZastupRepository extends JpaRepository<Zastup, String> {

	static final String sqlSelect = "select s from Zastup s left join fetch s.uzivatel uzivatel left join fetch s.uzivatelZastupce zastupce ";

	@Query(sqlSelect + "where s.guid = :guid")
	Zastup getDetail(String guid);

	@Query("select case when  count(s) > 0 then true else false end from Zastup s where s.guid != :guid and s.uzivatel.idUzivatel = :idUzivatel and s.uzivatelZastupce.idUzivatel = :idUzivatelZastupce and s.aktivita = true and s.platnostOd <= :datumDo and s.platnostDo >= :datumOd")
	boolean exists(String guid, String idUzivatel, String idUzivatelZastupce, Date datumOd, Date datumDo);

	@Query("select case when count(zastup) > 0 then true else false end from Zastup zastup where zastup.uzivatelZastupce.idUzivatel = :idUzivatel and zastup.uzivatel.idUzivatel = :idZastup and zastup.platnostOd <= now() and zastup.platnostDo >= now() and zastup.aktivita = true")
	boolean platnyZastup(String idUzivatel, String idZastup);
}
