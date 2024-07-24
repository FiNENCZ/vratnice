package cz.diamo.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Lokalita;

public interface LokalitaRepository extends JpaRepository<Lokalita, String> {
    static final String sqlSelect = "select s from Lokalita s left join fetch s.zavod zav ";

    @Query(sqlSelect + "where s.idLokalita = :idLokalita")
    Lokalita getDetail(String idLokalita);

	@Query(sqlSelect + "where s.idExterni = :idExterni")
	Lokalita getDetailByIdExterni(String idExterni);

	@Query("select count(s) from Lokalita s where s.idExterni = :idExterni and s.idLokalita != :idLokalita")
	Integer existsByIdExterni(String idExterni, String idLokalita);

	@Query(sqlSelect + "where zav.idZavod = :idZavod and s.aktivita = :aktivita order by s.nazev")
	List<Lokalita> getList(String idZavod, Boolean aktivita);
}
