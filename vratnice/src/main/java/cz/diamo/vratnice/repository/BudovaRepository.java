package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Budova;

public interface BudovaRepository extends JpaRepository<Budova, String> {
    static final String sqlSelect = "select s from Budova s ";

    @Query(sqlSelect + "where s.idBudova = :idBudova")
    Budova getDetail(String idBudova);

}
