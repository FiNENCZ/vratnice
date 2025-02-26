package cz.dp.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.dp.vratnice.entity.ZadostStav;

public interface ZadostStavRepository extends JpaRepository<ZadostStav, Integer> {

}
