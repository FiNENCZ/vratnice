package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.SpecialniKlicOznameniVypujcky;

public interface SpecialniKlicOznameniVypujckyRepository extends JpaRepository<SpecialniKlicOznameniVypujcky, String> {
    
    static final String sqlSelect = "select s from SpecialniKlicOznameniVypujcky s ";

    @Query(sqlSelect + "where s.idSpecialniKlicOznameniVypujcky = :idSpecialniKlicOznameniVypujcky")
    SpecialniKlicOznameniVypujcky getDetail(String idSpecialniKlicOznameniVypujcky);

    @Query(sqlSelect + "where s.klic = :klic AND s.aktivita = :aktivita")
    SpecialniKlicOznameniVypujcky getByKlic(Klic klic, Boolean aktivita);

    @Query("select case when count(u) > 0 then true else false end from SpecialniKlicOznameniVypujcky u where u.klic.idKlic = :idKlic")
    boolean existsByIdKlic(String idKlic);

}
