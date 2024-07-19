package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Lokalita;

public interface LokalitaRepository extends JpaRepository<Lokalita, String> {
    static final String sqlSelect = "select s from Lokalita s left join fetch s.zavod zav ";

    @Query(sqlSelect + "where s.idLokalita = :idLokalita")
    Lokalita getDetail(String idLokalita);

	@Query(sqlSelect + "where zav.idZavod = :idZavod and s.aktivita = :aktivita order by s.nazev")
	List<Lokalita> getList(String idZavod, Boolean aktivita);
}
