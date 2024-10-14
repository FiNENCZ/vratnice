package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.diamo.vratnice.entity.NavstevniListekStav;

public interface NavstevniListekStavRepository extends JpaRepository<NavstevniListekStav, Integer> {
}
