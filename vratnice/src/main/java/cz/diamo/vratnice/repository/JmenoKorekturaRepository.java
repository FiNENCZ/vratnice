package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.JmenoKorektura;

public interface JmenoKorekturaRepository extends JpaRepository<JmenoKorektura, String> {
    static final String sqlSelect = "select s from JmenoKorektura s ";

    @Query(sqlSelect + "where s.jmenoVstup = :jmenoVstup")
    JmenoKorektura getByJmenoVstup(String jmenoVstup);

    Boolean existsByJmenoVstup(String jmenoVstup);


}
