package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.vratnice.entity.SluzebniVozidlo;

public interface SluzebniVozidloRepository extends JpaRepository<SluzebniVozidlo, String> {
    static final String sqlSelect = "select s from SluzebniVozidlo s ";

    @Query(sqlSelect + "where s.idSluzebniVozidlo = :idSluzebniVozidlo")
    SluzebniVozidlo getDetail(String idSluzebniVozidlo);

    @Query(sqlSelect + "where UPPER(s.rz) = UPPER(:rz)")
    SluzebniVozidlo getByRz(String rz);

    Boolean existsByRz(String rz);
}
