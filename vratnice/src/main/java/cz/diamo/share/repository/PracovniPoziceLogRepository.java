package cz.diamo.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.PracovniPoziceLog;

public interface PracovniPoziceLogRepository extends JpaRepository<PracovniPoziceLog, String> {
        @Query("select log from PracovniPoziceLog log where (:ok is null or log.ok = :ok) and log.aktivita = true order by log.casVolani DESC")
        List<PracovniPoziceLog> getList(Boolean ok);

        @Query("select log from PracovniPoziceLog log where log.aktivita = true and log.ok = true order by log.casVolani DESC limit 1")
        PracovniPoziceLog getLastOk();
}
