package com.LMS.library_management.Repository;
import com.LMS.library_management.Models.Book;
import com.LMS.library_management.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByCategory(Category category);
    List<Book> findByAuthorId(UUID authorId);
    boolean existsByIsbn(String isbn);

    void deleteByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findById(UUID bookId);
}