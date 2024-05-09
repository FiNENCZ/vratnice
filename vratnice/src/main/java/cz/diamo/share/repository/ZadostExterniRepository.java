package cz.diamo.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.ZadostExterni;

public interface ZadostExterniRepository extends JpaRepository<ZadostExterni, String> {

	static final String sqlSelect = "select s from ZadostExterni s left join fetch s.uzivatel uzivatel left join fetch s.uzivatelVytvoril uzivatelVytvoril ";

	@Query(sqlSelect + "where s.idZadostExterni = :idZadostExterni")
	ZadostExterni getDetail(String idZadostExterni);
}
