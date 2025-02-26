package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.OpravneniPracovniPozice;
import cz.dp.share.entity.PracovniPozice;

public interface OpravneniPracovniPoziceRepository extends JpaRepository<OpravneniPracovniPozice, OpravneniPracovniPozice> {
    @Query("select pracovniPozice from PracovniPozice pracovniPozice where pracovniPozice.idPracovniPozice in (select v.idPracovniPozice from OpravneniPracovniPozice v where v.idOpravneni = :idOpravneni) order by pracovniPozice.nazev ASC")
    List<PracovniPozice> listPracovniPozice(String idOpravneni);

    @Query("select count(v) from OpravneniPracovniPozice v where v.idOpravneni = :idOpravneni and v.idPracovniPozice = :idPracovniPozice")
    Integer exists(String idOpravneni, String idPracovniPozice);

}
