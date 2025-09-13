package com.LMS.library_management.Controller;

import com.LMS.library_management.Dto.BorrowingTransactionDTO;
import com.LMS.library_management.Dto.TransactionReturn;
import com.LMS.library_management.Models.Book;
import com.LMS.library_management.Models.BorrowingTransaction;
import com.LMS.library_management.Repository.BookRepository;
import com.LMS.library_management.Repository.BorrowingTransactionRepository;
import com.LMS.library_management.Service.BorrowingTransactionService;
import com.LMS.library_management.Service.EmailServiceClient;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class BorrowingTransactionController {

    private final BorrowingTransactionService transactionService;
    private final BorrowingTransactionRepository transactionrepository;
    private final BookRepository bookRepository;
    private final Maplist maplist;
    private static final Logger logger = LoggerFactory.getLogger(BorrowingTransactionController.class);
    private  final ModelMapper Modelmapper;


    @PostMapping("/borrow")
    public ResponseEntity<TransactionReturn> borrowBook(@Valid @RequestBody BorrowingTransactionDTO dto) {
        BorrowingTransaction transaction = transactionService.borrowBook(dto);
        TransactionReturn borrowingTransactionDTO=Modelmapper.map(transaction, TransactionReturn.class);
        borrowingTransactionDTO.setBorrower_id(transaction.getBorrower().getId());
        borrowingTransactionDTO.setBook_isbn(transaction.getBook().getIsbn());

        logger.info("Book borrowed with transaction id {}", transaction.getId());
        return ResponseEntity.ok(borrowingTransactionDTO);
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<BorrowingTransactionDTO> returnBook(@PathVariable UUID id) {
        BorrowingTransaction transaction = transactionService.returnBook(id);
        BorrowingTransactionDTO borrowingTransactionDTO =Modelmapper.map(transaction,BorrowingTransactionDTO.class);
        borrowingTransactionDTO.setBook_isbn(transaction.getBook().getIsbn());
        borrowingTransactionDTO.setBorrower_id(transaction.getBorrower().getId());
        logger.info("Book returned with transaction id {}", transaction.getId());
        return ResponseEntity.ok(borrowingTransactionDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingTransactionDTO> getTransactionById(@PathVariable UUID id) {
        BorrowingTransaction transaction = transactionService.getTransactionById(id);
        BorrowingTransactionDTO borrowingTransactionDTO =Modelmapper.map(transaction,BorrowingTransactionDTO.class);
        borrowingTransactionDTO.setBook_isbn(transaction.getBook().getIsbn());
        borrowingTransactionDTO.setBorrower_id(transaction.getBorrower().getId());

        return ResponseEntity.ok(borrowingTransactionDTO);
    }

    @GetMapping
    public ResponseEntity<List<BorrowingTransactionDTO>> getAllTransactions() {
        List<BorrowingTransaction> transactions = transactionService.getAllTransactions();
        List<BorrowingTransactionDTO> borrowingTransactionDTO = maplist.mapList(transactions,BorrowingTransactionDTO.class);
        for(int i=0;i<transactions.size();i++){
            borrowingTransactionDTO.get(i).setBorrower_id(transactions.get(i).getBorrower().getId());
            borrowingTransactionDTO.get(i).setBook_isbn(transactions.get(i).getBook().getIsbn());
        }

        return ResponseEntity.ok(borrowingTransactionDTO);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public void deleteTransaction(UUID id) {
        BorrowingTransaction transaction = transactionrepository.findById(id);

        // Mark the book as available again
        Book book = transaction.getBook();
        if (book != null) {
            book.setAvailable(true);
            bookRepository.save(book);
        }

        // Delete the transaction
        transactionrepository.delete(transaction);
        logger.info("Deleted transaction {} and set book {} as available", id, book != null ? book.getIsbn() : "N/A");
    }

}
