package cz.diamo.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.TmpOpravneniVse;

public interface TmpOpravneniVseRepository extends JpaRepository<TmpOpravneniVse, String> {
	@Modifying
	@Query(value = "delete from " + Constants.SCHEMA
			+ ".tmp_opravneni_vse where id_uzivatel = :idUzivatel", nativeQuery = true)
	void deleteByUzivatel(String idUzivatel);

	@Modifying
	@Query(value = "insert into " + Constants.SCHEMA + ".tmp_opravneni_vse" + " select distinct"
			+ " concat(uzivatel_opravneni.id_uzivatel,'#',opravneni_role.authority) as id_tmp_opravneni_vse,"
			+ " uzivatel_opravneni.id_uzivatel," + " opravneni_role.authority" + " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA + ".opravneni role on (role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " where" + " role.id_opravneni_typ_pristupu = 2"
			+ " and uzivatel_opravneni.id_uzivatel = :idUzivatel", nativeQuery = true)
	void insertByUzivatel(String idUzivatel);

	@Query(value = "select count(tmp_opravneni_vse.id_uzivatel) from " + Constants.SCHEMA
			+ ".tmp_opravneni_vse tmp_opravneni_vse where tmp_opravneni_vse.id_uzivatel = :idUzivatel and tmp_opravneni_vse.authority in :role", nativeQuery = true)
	Integer exists(String idUzivatel, List<String> role);
}
