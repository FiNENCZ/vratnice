package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Lokalita;

public interface LokalitaRepository extends JpaRepository<Lokalita, String> {
    static final String sqlSelect = "select s from Lokalita s ";

    @Query(sqlSelect + "where s.idLokalita = :idLokalita")
    Lokalita getDetail(String idLokalita);

}
