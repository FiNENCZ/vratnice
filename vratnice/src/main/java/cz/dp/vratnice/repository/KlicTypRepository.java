package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.dp.vratnice.entity.KlicTyp;

public interface KlicTypRepository extends JpaRepository<KlicTyp, Integer> {
    
}
