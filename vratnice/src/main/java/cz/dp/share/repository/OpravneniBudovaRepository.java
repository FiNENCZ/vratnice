package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.Budova;
import cz.dp.share.entity.OpravneniBudova;

public interface OpravneniBudovaRepository extends JpaRepository<OpravneniBudova, OpravneniBudova> {
    @Query("select budova from Budova budova where budova.idBudova in (select v.idBudova from OpravneniBudova v where v.idOpravneni = :idOpravneni) order by budova.nazev ASC")
    List<Budova> listBudova(String idOpravneni);

    @Query("select count(v) from OpravneniBudova v where v.idOpravneni = :idOpravneni and v.idBudova = :idBudova")
    Integer exists(String idOpravneni, String idBudova);

}
