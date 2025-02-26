package cz.dp.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.dp.share.entity.Databaze;

public interface DatabazeRepository extends JpaRepository<Databaze, Integer>  {
    
}
