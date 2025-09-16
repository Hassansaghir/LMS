package com.LMS.Controller;

import com.LMS.Dto.BorrowingTransactionDTO;
import com.LMS.Dto.CustomResponse;
import com.LMS.Dto.TransactionReturn;
import com.LMS.Models.Book;
import com.LMS.Models.BorrowingTransaction;
import com.LMS.Repository.BookRepository;
import com.LMS.Repository.BorrowingTransactionRepository;
import com.LMS.Service.BorrowingTransactionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class BorrowingTransactionController {

    private final BorrowingTransactionService transactionService;
    private final BorrowingTransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final Maplist maplist;
    private static final Logger logger = LoggerFactory.getLogger(BorrowingTransactionController.class);
    private final ModelMapper modelMapper;

    @PostMapping("/borrow")
    public CustomResponse<TransactionReturn> borrowBook(@Valid @RequestBody BorrowingTransactionDTO dto) {
        try {
            BorrowingTransaction transaction = transactionService.borrowBook(dto);
            TransactionReturn borrowingTransactionDTO = modelMapper.map(transaction, TransactionReturn.class);
            borrowingTransactionDTO.setBorrower_id(transaction.getBorrower().getId());
            borrowingTransactionDTO.setBook_isbn(transaction.getBook().getIsbn());

            logger.info("Book borrowed with transaction id {}", transaction.getId());
            return new CustomResponse<>("success", "Book borrowed successfully", borrowingTransactionDTO);
        } catch (Exception e) {
            logger.error("Error borrowing book: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to borrow book", null);
        }
    }

    @PostMapping("/return/{id}")
    public CustomResponse<BorrowingTransactionDTO> returnBook(@PathVariable UUID id) {
        try {
            BorrowingTransaction transaction = transactionService.returnBook(id);
            BorrowingTransactionDTO borrowingTransactionDTO = modelMapper.map(transaction, BorrowingTransactionDTO.class);
            borrowingTransactionDTO.setBook_isbn(transaction.getBook().getIsbn());
            borrowingTransactionDTO.setBorrower_id(transaction.getBorrower().getId());

            logger.info("Book returned with transaction id {}", transaction.getId());
            return new CustomResponse<>("success", "Book returned successfully", borrowingTransactionDTO);
        } catch (Exception e) {
            logger.error("Error returning book: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to return book", null);
        }
    }

    @GetMapping("/{id}")
    public CustomResponse<BorrowingTransactionDTO> getTransactionById(@PathVariable UUID id) {
        try {
            BorrowingTransaction transaction = transactionService.getTransactionById(id);
            if (transaction == null) {
                return new CustomResponse<>("error", "Transaction not found", null);
            }
            BorrowingTransactionDTO borrowingTransactionDTO = modelMapper.map(transaction, BorrowingTransactionDTO.class);
            borrowingTransactionDTO.setBook_isbn(transaction.getBook().getIsbn());
            borrowingTransactionDTO.setBorrower_id(transaction.getBorrower().getId());

            return new CustomResponse<>("success", "Transaction retrieved successfully", borrowingTransactionDTO);
        } catch (Exception e) {
            logger.error("Error retrieving transaction: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve transaction", null);
        }
    }

    @GetMapping
    public CustomResponse<List<BorrowingTransactionDTO>> getAllTransactions() {
        try {
            List<BorrowingTransaction> transactions = transactionService.getAllTransactions();
            List<BorrowingTransactionDTO> transactionDTOs = maplist.mapList(transactions, BorrowingTransactionDTO.class);
            for (int i = 0; i < transactions.size(); i++) {
                transactionDTOs.get(i).setBorrower_id(transactions.get(i).getBorrower().getId());
                transactionDTOs.get(i).setBook_isbn(transactions.get(i).getBook().getIsbn());
            }
            return new CustomResponse<>("success", "Transactions retrieved successfully", transactionDTOs);
        } catch (Exception e) {
            logger.error("Error retrieving transactions: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve transactions", null);
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteTransaction(@PathVariable UUID id) {
        try {
            BorrowingTransaction transaction = transactionRepository.findById(id);
            if (transaction == null) {
                return new CustomResponse<>("error", "Transaction not found", null);
            }

            // Mark the book as available again
            Book book = transaction.getBook();
            if (book != null) {
                book.setAvailable(true);
                bookRepository.save(book);
            }

            // Delete the transaction
            transactionRepository.delete(transaction);
            logger.info("Deleted transaction {} and set book {} as available", id, book != null ? book.getIsbn() : "N/A");

            return new CustomResponse<>("success", "Transaction deleted successfully", null);
        } catch (Exception e) {
            logger.error("Error deleting transaction: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to delete transaction", null);
        }
    }
}
