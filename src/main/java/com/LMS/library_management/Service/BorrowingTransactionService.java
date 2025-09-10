package com.LMS.library_management.Service;
import com.LMS.library_management.Dto.EmailRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import com.LMS.library_management.Dto.BorrowingTransactionDTO;
import com.LMS.library_management.Models.Book;
import com.LMS.library_management.Models.Borrower;
import com.LMS.library_management.Models.BorrowingTransaction;
import com.LMS.library_management.Models.BorrowingStatus;
import com.LMS.library_management.Exception.BadRequestException;
import com.LMS.library_management.Exception.ResourceNotFoundException;
import com.LMS.library_management.Repository.BookRepository;
import com.LMS.library_management.Repository.BorrowerRepository;
import com.LMS.library_management.Repository.BorrowingTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Data
@RequiredArgsConstructor
public class BorrowingTransactionService {

    private final ModelMapper modelMapper;
    private final BorrowingTransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private static final Logger logger = LoggerFactory.getLogger(BorrowingTransactionService.class);
    @Value("${borrower.transaction.limit}")
    private int transactionLimit;
    private final EmailServiceClient emailServiceClient; // Feign client injected

    public BorrowingTransaction borrowBook(BorrowingTransactionDTO dto) {

        // Check transaction limit
        int currentTransactionCount = transactionRepository.countActiveByBorrowerId(dto.getBorrower_id());
        if (currentTransactionCount >= transactionLimit) {
            throw new IllegalStateException(
                    "Borrower has reached the maximum allowed borrow transactions: " + transactionLimit);
        }

        // Fetch Book and Borrower entities
        Book book = bookRepository.findByIsbn(dto.getBook_isbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with isbn " + dto.getBook_isbn()));

        if (!book.isAvailable()) {
            throw new BadRequestException("Book with isbn " + dto.getBook_isbn() + " is currently unavailable");
        }

        Borrower borrower = borrowerRepository.findById(dto.getBorrower_id())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id " + dto.getBook_isbn()));

        // Map DTO to entity, skipping id, book, and borrower
        BorrowingTransaction transaction = modelMapper.map(dto, BorrowingTransaction.class);
        transaction.setBook(book);           // manually assign entity
        transaction.setBorrower(borrower);   // manually assign entity
        transaction.setStatus(BorrowingStatus.BORROWED);

        // Mark book as unavailable
        book.setAvailable(false);
        bookRepository.save(book);

        BorrowingTransaction savedTransaction = transactionRepository.save(transaction);

        // Send email notification
        String message = "Book \"" + book.getTitle() + "\" borrowed successfully.";
        EmailRequest emailRequest = new EmailRequest(borrower.getEmail(), message);
        emailServiceClient.sendEmail(emailRequest);

        return savedTransaction;
    }


    public BorrowingTransaction returnBook(UUID transactionId) {
        BorrowingTransaction transaction = transactionRepository.findById(transactionId);

        if (transaction.getStatus() == BorrowingStatus.RETURNED) {
            throw new BadRequestException("Book has already been returned for transaction id " + transactionId);
        }

        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus(BorrowingStatus.RETURNED);

        // Mark book as available
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        BorrowingTransaction updated = transactionRepository.save(transaction);
        logger.info("Book id {} returned for transaction id {}", book.getId(), transactionId);
        return updated;
    }

    public BorrowingTransaction getTransactionById(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<BorrowingTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id " + id);
        }
        transactionRepository.deleteById(id);
        logger.info("Deleted transaction with id {}", id);
    }
}
