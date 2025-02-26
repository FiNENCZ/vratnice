package cz.dp.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.dp.share.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
