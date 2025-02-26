package cz.dp.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.ZdrojovyText;

public interface ZdrojovyTextRepository extends JpaRepository<ZdrojovyText, Integer>  {
    static final String sqlSelect = "select s from ZdrojovyText s";

	@Query(sqlSelect + " where s.culture = :culture and s.hash = :hash")
	ZdrojovyText getDetail(String culture, String hash); 
}
