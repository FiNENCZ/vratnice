package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.UzivatelModul;

public interface UzivatelModulRepository extends JpaRepository<UzivatelModul, UzivatelModul> {

    @Query("select v.modul from UzivatelModul v where v.idUzivatel = :idUzivatel order by v.modul ASC")
    List<String> listModul(String idUzivatel);
}
