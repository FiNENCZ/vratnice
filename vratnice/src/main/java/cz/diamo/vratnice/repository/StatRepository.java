package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.diamo.vratnice.entity.Stat;

public interface StatRepository extends JpaRepository<Stat, Integer> {

}
