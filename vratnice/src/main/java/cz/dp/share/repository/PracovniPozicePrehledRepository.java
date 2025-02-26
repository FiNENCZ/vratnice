package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.PracovniPozicePrehled;

public interface PracovniPozicePrehledRepository extends JpaRepository<PracovniPozicePrehled, String> {

	@Query(value = "select concat(pracpozice.id_pracovni_pozice, '#', uzivatel.id_uzivatel) as id, pracpozice.id_pracovni_pozice, pracpozice.sap_id, pracpozice.sap_id_nadrizeny, pracpozice.nazev, pracpozice.zkratka, uzivatel.sap_id as uzivatel_sap_id, uzivatel.nazev as uzivatel_nazev, uzivatel.prijmeni, uzivatel.jmeno, uzivatel.email, uzivatel.tel, zavod.nazev as zavod_nazev, zavod.sap_id as zavod_sap_id from "
			+ Constants.SCHEMA + ".opravneni_pracovni_pozice v, " + Constants.SCHEMA
			+ ".pracovni_pozice pracpozice left join " + Constants.SCHEMA
			+ ".uzivatel uzivatel on (uzivatel.id_pracovni_pozice = pracpozice.id_pracovni_pozice and (:idZavod is null or uzivatel.id_zavod = :idZavod)) left join "
			+ Constants.SCHEMA
			+ ".zavod on (zavod.id_zavod = uzivatel.id_zavod) where pracpozice.id_pracovni_pozice = v.id_pracovni_pozice and v.id_opravneni = :idOpravneni order by pracpozice.nazev ASC", nativeQuery = true)
	List<PracovniPozicePrehled> listPrehled(String idOpravneni, String idZavod);
}
