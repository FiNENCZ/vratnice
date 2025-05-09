package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.VozidloTyp;

public interface VozidloTypRepository extends JpaRepository<VozidloTyp, Integer> {
    static final String sqlSelect = "select s from VozidloTyp s ";
    
    @Query(sqlSelect + "where s.idVozidloTyp = :idVozidloTyp")
    VozidloTyp getDetail(Integer idVozidloTyp);

    @Query(sqlSelect + "where s.nazevResx = :nazevResx")
    VozidloTyp getDetailByNazevResx(String nazevResx);

    @Query("SELECT vt FROM VozidloTyp vt JOIN ZdrojovyText zt ON vt.nazevResx = zt.hash WHERE zt.text = :text")
    VozidloTyp getByNazev(String text);

}
