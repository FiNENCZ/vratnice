package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.vratnice.entity.SluzebniVozidlo;

public interface SluzebniVozidloRepository extends JpaRepository<SluzebniVozidlo, String> {
    static final String sqlSelect = "select s from SluzebniVozidlo s ";

    @Query(sqlSelect + "where s.idSluzebniVozidlo = :idSluzebniVozidlo")
    SluzebniVozidlo getDetail(String idSluzebniVozidlo);

    @Query(sqlSelect + "where s.rz = :rz")
    SluzebniVozidlo getByRz(String rz);

    Boolean existsByRz(String rz);

    @Query(sqlSelect + "where s.stav = :stav")
    List<SluzebniVozidlo> getSluzebniVozidloByStav(String stav);

    @Query(sqlSelect + "where s.aktivita = :aktivita")
    List<SluzebniVozidlo> findByAktivita(Boolean aktivita);

}
