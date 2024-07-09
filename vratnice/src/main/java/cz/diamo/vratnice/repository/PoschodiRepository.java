package cz.diamo.vratnice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.diamo.vratnice.entity.Poschodi;

public interface PoschodiRepository extends JpaRepository<Poschodi, String> {

}
