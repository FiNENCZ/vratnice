package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.Stat;

public interface StatRepository extends JpaRepository<Stat, Integer> {

    @Query("SELECT vt FROM Stat vt JOIN ZdrojovyText zt ON vt.nazevResx = zt.hash WHERE zt.text = :text")
    Stat getByNazev(String text);

}
