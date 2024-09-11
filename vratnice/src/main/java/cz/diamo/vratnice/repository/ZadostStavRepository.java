package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.diamo.vratnice.entity.ZadostStav;

public interface ZadostStavRepository extends JpaRepository<ZadostStav, Integer> {

}
