package com.LMS.library_management.Repository;

import com.LMS.library_management.Models.Book;
import com.LMS.library_management.Models.BorrowingTransaction;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingTransactionRepository extends JpaRepository<BorrowingTransaction, Long> {


    List<BorrowingTransaction> findByBookId(UUID bookId);

    List<BorrowingTransaction> findByBorrowerId(UUID borrowerId);

    List<BorrowingTransaction> findByBookIsbn(String isbn); // use Book's PK type

    int countActiveByBorrowerId(@NotNull(message = "Borrower ID is required") UUID borrowerId);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    BorrowingTransaction findById(UUID transactionId);
}
