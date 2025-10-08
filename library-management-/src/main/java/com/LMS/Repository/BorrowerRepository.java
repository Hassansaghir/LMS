package com.LMS.Repository;

import com.LMS.Models.Borrower;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower,Long> {
    Optional<Borrower> findById(@NotNull(message = "Borrower ID is required") UUID borrowerId);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}


