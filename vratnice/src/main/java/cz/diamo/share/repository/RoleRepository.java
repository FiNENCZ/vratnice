package cz.diamo.share.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.diamo.share.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
