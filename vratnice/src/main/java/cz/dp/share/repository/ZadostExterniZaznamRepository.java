package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.ZadostExterni;
import cz.dp.share.entity.ZadostExterniZaznam;

public interface ZadostExterniZaznamRepository extends JpaRepository<ZadostExterniZaznam, ZadostExterniZaznam> {

	@Query("select s from ZadostExterni s where s.idZadostExterni in (select v.idZadostExterni from ZadostExterniZaznam v where v.idZaznam = :idZaznam) order by s.cas DESC")
	List<ZadostExterni> listZadost(String idZaznam);

	@Query("select v.idZaznam from ZadostExterniZaznam v where v.idZadostExterni = :idZadostExterni")
	List<String> listIdZaznam(String idZadostExterni);

	@Query("select v.idZadostExterni from ZadostExterniZaznam v where v.idZaznam = :idZaznam")
	List<String> listIdExterni(String idZaznam);

	@Query("select case when count(v) > 0 then true else false end from ZadostExterniZaznam v where v.idZadostExterni = :idZadostExterni and v.idZaznam = :idZaznam")
	Boolean existsById(String idZadostExterni, String idZaznam);

	@Query("select case when count(v) > 0 then true else false end from ZadostExterniZaznam v where v.idZaznam = :idZaznam")
	Boolean existsByIdZaznam(String idZaznam);

}
