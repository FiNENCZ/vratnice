package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Poschodi;

public interface PoschodiRepository extends JpaRepository<Poschodi, String> {
    static final String sqlSelect = "select s from Poschodi s ";

    @Query(sqlSelect + "where s.idPoschodi = :idPoschodi")
    Poschodi getDetail(String idPoschodi);

}
