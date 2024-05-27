package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.ZadostKlic;

public interface ZadostKlicRepository extends JpaRepository<ZadostKlic, String> {
    static final String sqlSelect = "select s from ZadostKlic s ";

    @Query(sqlSelect + "where s.idZadostKlic = :idZadostKlic")
    ZadostKlic getDetail(String idZadostKlic);
}
