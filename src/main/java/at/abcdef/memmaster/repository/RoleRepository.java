package at.abcdef.memmaster.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import at.abcdef.memmaster.model.ERole;
import at.abcdef.memmaster.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
	String ROLE_BY_NAME_CACHE = "roleByName";

	@Cacheable(cacheNames = ROLE_BY_NAME_CACHE)
	Optional<Role> findByName(ERole name);
}
