package com.LMS.Repository;

import com.LMS.Models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String authorName);

    Optional<Author> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
