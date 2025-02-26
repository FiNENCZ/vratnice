package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cz.dp.vratnice.entity.NavstevniListekTyp;
import cz.dp.vratnice.entity.UzivatelNavstevniListekTyp;

public interface UzivatelNavstevniListekTypRepository extends JpaRepository<UzivatelNavstevniListekTyp,UzivatelNavstevniListekTyp> {

    @Query("SELECT nlt FROM NavstevniListekTyp nlt JOIN UzivatelNavstevniListekTyp unt ON nlt.idNavstevniListekTyp = unt.idNavstevniListekTyp WHERE unt.idUzivatel = :idUzivatel")
    NavstevniListekTyp findNavstevniListekTypByUzivatelId(@Param("idUzivatel") String idUzivatel);


}
