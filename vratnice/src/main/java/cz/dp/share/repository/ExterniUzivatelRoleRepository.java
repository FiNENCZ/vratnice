package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.ExterniRole;
import cz.dp.share.entity.ExterniUzivatelRole;

public interface ExterniUzivatelRoleRepository extends JpaRepository<ExterniUzivatelRole, ExterniUzivatelRole> {
    @Query("select r from ExterniRole r where r.authority in (select v.authority from ExterniUzivatelRole v where v.idExterniUzivatel = :idExterniUzivatel) order by r.authority ASC")
    List<ExterniRole> listRole(String idExterniUzivatel);

    @Query("select count(v) from ExterniUzivatelRole v where v.idExterniUzivatel = :idExterniUzivatel and v.authority = :authority")
    Integer exists(String idExterniUzivatel, String authority);

}
