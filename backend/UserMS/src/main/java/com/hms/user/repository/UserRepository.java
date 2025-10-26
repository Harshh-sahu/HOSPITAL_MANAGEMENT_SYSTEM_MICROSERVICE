package com.hms.user.repository;

import java.util.List;
import java.util.Optional;

import com.hms.user.dto.MonthlyRoleCountDTO;
import com.hms.user.dto.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long >{


    Optional<User> findByEmail(String email);

    @Query("SELECT new com.hms.user.dto.MonthlyRoleCountDTO( cast( FUNCTION('MONTHNAME',a.createdAt)as string ),count(a)) FROM User a where a.role = ?1 AND YEAR(a.createdAt)=YEAR(CURRENT_DATE) GROUP BY FUNCTION('MONTH',a.createdAt),cast( FUNCTION('MONTHNAME',a.createdAt)as string )ORDER BY FUNCTION('MONTH',a.createdAt)")
    List<MonthlyRoleCountDTO> countRegistrationsByRoleGroupedByMonth(Roles role);

}
