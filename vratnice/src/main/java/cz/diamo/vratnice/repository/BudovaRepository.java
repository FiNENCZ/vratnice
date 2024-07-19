package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Budova;

public interface BudovaRepository extends JpaRepository<Budova, String> {
    static final String sqlSelect = "select s from Budova s left join fetch s.lokalita lok ";

    @Query(sqlSelect + "where s.idBudova = :idBudova")
    Budova getDetail(String idBudova);

	@Query(sqlSelect + "where lok.idLokalita = :idLokalita and s.aktivita = :aktivita order by s.nazev")
	List<Budova> getList(String idLokalita, Boolean aktivita);

}
