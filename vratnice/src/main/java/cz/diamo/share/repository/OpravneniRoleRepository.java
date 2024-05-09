package cz.diamo.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.OpravneniRole;
import cz.diamo.share.entity.Role;

public interface OpravneniRoleRepository extends JpaRepository<OpravneniRole, OpravneniRole> {
    @Query("select r from Role r where r.authority in (select v.authority from OpravneniRole v where v.idOpravneni = :idOpravneni) order by r.authority ASC")
    List<Role> listRole(String idOpravneni);

    @Query("select count(v) from OpravneniRole v where v.idOpravneni = :idOpravneni and v.authority = :authority")
    Integer exists(String idOpravneni, String authority);

}
