package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.JmenoKorektura;

public interface JmenoKorekturaRepository extends JpaRepository<JmenoKorektura, String> {
    static final String sqlSelect = "select s from JmenoKorektura s ";

    @Query(sqlSelect + "where s.jmenoVstup = :jmenoVstup")
    JmenoKorektura getByJmenoVstup(String jmenoVstup);

    Boolean existsByJmenoVstup(String jmenoVstup);


}
