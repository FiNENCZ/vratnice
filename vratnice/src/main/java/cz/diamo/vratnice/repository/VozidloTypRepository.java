package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.VozidloTyp;

public interface VozidloTypRepository extends JpaRepository<VozidloTyp, Integer> {
    static final String sqlSelect = "select s from VozidloTyp s ";
    
    @Query(sqlSelect + "where s.idVozidloTyp = :idVozidloTyp")
    VozidloTyp getDetail(Integer idVozidloTyp);

    @Query(sqlSelect + "where s.nazevResx = :nazevResx")
    VozidloTyp getDetailByNazevResx(String nazevResx);

}
