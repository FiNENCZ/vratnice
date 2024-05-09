package cz.diamo.share.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.HistorieZaznamu;

public interface HistorieZaznamuRepository extends JpaRepository<HistorieZaznamu, BigInteger> {

    @Query(value = "select max(historie.cas_zmn) from " + Constants.SCHEMA  + ".historie_zaznamu historie where historie.table_name = :table and historie.id_zaznamu = :idZaznamu", nativeQuery = true)
    Date maxCasZmn(String table, String idZaznamu);

    @Query(value = "select max(historie.cas_zmn) from " + Constants.SCHEMA  + ".historie_zaznamu historie where historie.table_name = :table and historie.id_zaznamu = :idZaznamu and historie.table_column not in :notInColumn", nativeQuery = true)
    Date maxCasZmn(String table, String idZaznamu, List<String> notInColumn);
}
