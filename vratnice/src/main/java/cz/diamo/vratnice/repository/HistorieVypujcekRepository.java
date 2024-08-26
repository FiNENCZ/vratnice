package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.HistorieVypujcekAkce;
import cz.diamo.vratnice.entity.ZadostKlic;


public interface HistorieVypujcekRepository extends JpaRepository<HistorieVypujcek, String> {
    static final String sqlSelect = "select s from HistorieVypujcek s ";

    @Query(sqlSelect + "where s.zadostKlic = :zadostKlic")
    List<HistorieVypujcek> findByZadostKlic(ZadostKlic zadostKlic);

    // Dotaz pro získání posledního HistorieVypujcekAkce podle idKlic
    @Query("select s.akce from HistorieVypujcek s where s.zadostKlic.klic.idKlic = :idKlic order by s.datum desc limit 1")
    HistorieVypujcekAkce findLastAkceByIdKlic(String idKlic);

    @Query(sqlSelect + "where s.zadostKlic.klic.kodCipu = :kodCipu order by s.datum desc limit 1")
    HistorieVypujcek findLaskVypujckaByKodCipu(String kodCipu);
}
