package com.klx.ebookbackend.repository;

import com.klx.ebookbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Procedure(procedureName = "validate_user")
    int validateUser(@Param("p_username") String username, @Param("p_password") String password);

    @Procedure(procedureName = "validate_user_is_enabled")
    int validateUserIsEnabled(@Param("p_username") String username);

    @Procedure(procedureName = "insert_and_update_password")
    void changePassword(@Param("p_user_id") int userId, @Param("p_new_password") String newPassword);

    User findByUsername(String username);
}
