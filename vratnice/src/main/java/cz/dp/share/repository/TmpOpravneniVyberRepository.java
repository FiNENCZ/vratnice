package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.TmpOpravneniVyber;

public interface TmpOpravneniVyberRepository extends JpaRepository<TmpOpravneniVyber, String> {
	@Modifying
	@Query(value = "delete from " + Constants.SCHEMA
			+ ".tmp_opravneni_vyber where id_uzivatel = :idUzivatel", nativeQuery = true)
	void deleteByuzivatel(String idUzivatel);

	@Modifying
	@Query(value = "insert into " + Constants.SCHEMA + ".tmp_opravneni_vyber" + " select distinct"
			+ " concat(uzivatel_opravneni.id_uzivatel,'#',opravneni_role.authority, '#', podrizeny.id_uzivatel) as id_tmp_opravneni_vyber,"
			+ " uzivatel_opravneni.id_uzivatel as id_uzivatel,"
			+ " podrizeny.id_uzivatel as id_uzivatel_podrizeny,"
			+ " opravneni_role.authority as authority"
			+ " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA
			+ ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_pracovni_pozice opravneni_pracovni_pozice on (opravneni_pracovni_pozice.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel podrizeny on (podrizeny.id_pracovni_pozice = opravneni_pracovni_pozice.id_pracovni_pozice)"
			+ " where" + " opravneni.id_opravneni_typ_pristupu = 3"
			+ " and uzivatel_opravneni.id_uzivatel = :idUzivatel" + " union" + " select distinct"
			+ " concat(uzivatel_opravneni.id_uzivatel,'#',opravneni_role.authority, '#', podrizeny.id_uzivatel) as tmp_opravneni_vyber,"
			+ " uzivatel_opravneni.id_uzivatel as id_uzivatel,"
			+ " podrizeny.id_uzivatel as id_uzivatel_podrizeny,"
			+ " opravneni_role.authority"
			+ " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA
			+ ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel uzivatel on (uzivatel.id_uzivatel = uzivatel_opravneni.id_uzivatel)"
			+ " join " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene podrizene_pozice on (podrizene_pozice.id_pracovni_pozice = uzivatel.id_pracovni_pozice and podrizene_pozice.primy_podrizeny = true and podrizene_pozice.aktivita = true)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel podrizeny on (podrizeny.id_pracovni_pozice = podrizene_pozice.id_pracovni_pozice_podrizeny)"
			+ " where"
			+ " opravneni.id_opravneni_typ_pristupu = 4" + " and uzivatel_opravneni.id_uzivatel = :idUzivatel"
			+ " union" + " select distinct"
			+ " concat(uzivatel_opravneni.id_uzivatel,'#',opravneni_role.authority, '#', podrizeny.id_uzivatel) as tmp_opravneni_vyber,"
			+ " uzivatel_opravneni.id_uzivatel as id_uzivatel,"
			+ " podrizeny.id_uzivatel as id_uzivatel_podrizeny,"
			+ " opravneni_role.authority"
			+ " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA
			+ ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel uzivatel on (uzivatel.id_uzivatel = uzivatel_opravneni.id_uzivatel)"
			+ " join " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene podrizene_pozice on (podrizene_pozice.id_pracovni_pozice = uzivatel.id_pracovni_pozice and podrizene_pozice.aktivita = true)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel podrizeny on (podrizeny.id_pracovni_pozice = podrizene_pozice.id_pracovni_pozice_podrizeny)"
			+ " where"
			+ " opravneni.id_opravneni_typ_pristupu = 5"
			+ " and uzivatel_opravneni.id_uzivatel = :idUzivatel", nativeQuery = true)
	void insertByUzivatel(String idUzivatel);

	@Modifying
	@Query(value = "insert into " + Constants.SCHEMA + ".tmp_opravneni_vyber" + " select distinct"
			+ " concat(:idUzivatel,'#',opravneni_role.authority, '#', podrizeny.id_uzivatel) as id_tmp_opravneni_vyber,"
			+ " :idUzivatel as id_uzivatel,"
			+ " podrizeny.id_uzivatel as id_uzivatel_podrizeny,"
			+ " opravneni_role.authority as authority"
			+ " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA
			+ ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_pracovni_pozice opravneni_pracovni_pozice on (opravneni_pracovni_pozice.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel podrizeny on (podrizeny.id_pracovni_pozice = opravneni_pracovni_pozice.id_pracovni_pozice)"
			+ " where" + " opravneni.id_opravneni_typ_pristupu = 3"
			+ " and uzivatel_opravneni.id_uzivatel = :idZastup" + " union" + " select distinct"
			+ " concat(:idUzivatel,'#',opravneni_role.authority, '#', podrizeny.id_uzivatel) as tmp_opravneni_vyber,"
			+ " :idUzivatel as id_uzivatel,"
			+ " podrizeny.id_uzivatel as id_uzivatel_podrizeny,"
			+ " opravneni_role.authority"
			+ " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA
			+ ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel uzivatel on (uzivatel.id_uzivatel = uzivatel_opravneni.id_uzivatel)"
			+ " join " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene podrizene_pozice on (podrizene_pozice.id_pracovni_pozice = uzivatel.id_pracovni_pozice and podrizene_pozice.primy_podrizeny = true and podrizene_pozice.aktivita = true)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel podrizeny on (podrizeny.id_pracovni_pozice = podrizene_pozice.id_pracovni_pozice_podrizeny)"
			+ " where"
			+ " opravneni.id_opravneni_typ_pristupu = 4" + " and uzivatel_opravneni.id_uzivatel = :idZastup"
			+ " union" + " select distinct"
			+ " concat(:idUzivatel,'#',opravneni_role.authority, '#', podrizeny.id_uzivatel) as tmp_opravneni_vyber,"
			+ " :idUzivatel as id_uzivatel,"
			+ " podrizeny.id_uzivatel as id_uzivatel_podrizeny,"
			+ " opravneni_role.authority"
			+ " from"
			+ " " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni"
			+ " join " + Constants.SCHEMA
			+ ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = uzivatel_opravneni.id_opravneni)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel uzivatel on (uzivatel.id_uzivatel = uzivatel_opravneni.id_uzivatel)"
			+ " join " + Constants.SCHEMA
			+ ".pracovni_pozice_podrizene podrizene_pozice on (podrizene_pozice.id_pracovni_pozice = uzivatel.id_pracovni_pozice and podrizene_pozice.aktivita = true)"
			+ " join " + Constants.SCHEMA
			+ ".uzivatel podrizeny on (podrizeny.id_pracovni_pozice = podrizene_pozice.id_pracovni_pozice_podrizeny)"
			+ " where"
			+ " opravneni.id_opravneni_typ_pristupu = 5"
			+ " and uzivatel_opravneni.id_uzivatel = :idZastup", nativeQuery = true)
	void insertByUzivatelZastup(String idUzivatel, String idZastup);

	@Query(value = "select count(tmp_opravneni_vyber.id_uzivatel) from " + Constants.SCHEMA
			+ ".tmp_opravneni_vyber tmp_opravneni_vyber where tmp_opravneni_vyber.id_uzivatel = :idUzivatel and tmp_opravneni_vyber.id_uzivatel_podrizeny = :idUzivatelPodrizeny and tmp_opravneni_vyber.authority in :role", nativeQuery = true)
	Integer exists(String idUzivatel, String idUzivatelPodrizeny, List<String> role);
}
