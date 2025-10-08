package com.LMS.Service;
import com.LMS.Dto.EmailRequest;
import com.LMS.Dto.Type;
import jakarta.transaction.Transactional;
import lombok.Data;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import com.LMS.Dto.BorrowingTransactionDTO;
import com.LMS.Dto.TransactionResponse;
import com.LMS.Models.Book;
import com.LMS.Dto.CreateTransactionRequest;
import com.LMS.Models.Borrower;
import com.LMS.Models.BorrowingTransaction;
import com.LMS.Models.BorrowingStatus;
import com.LMS.Exception.BadRequestException;
import com.LMS.Exception.ResourceNotFoundException;
import com.LMS.Repository.BookRepository;
import com.LMS.Repository.BorrowerRepository;
import com.LMS.Repository.BorrowingTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Data
@RequiredArgsConstructor
public class BorrowingTransactionService {

    private final ModelMapper modelMapper;
    private final CmsClient cmsClient;
    private final BorrowingTransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private static final Logger logger = LoggerFactory.getLogger(BorrowingTransactionService.class);
    @Value("${borrower.transaction.limit}")
    private int transactionLimit;
    private final EmailServiceClient emailServiceClient; // Feign client injected

    @Transactional
    public BorrowingTransaction borrowBook(BorrowingTransactionDTO dto) {
        // Check transaction limit
        int currentTransactionCount = transactionRepository.countActiveByBorrowerId(dto.getBorrower_id());
        if (currentTransactionCount >= transactionLimit) {
            throw new IllegalStateException(
                    "Borrower has reached the maximum allowed borrow transactions: " + transactionLimit);
        }

        // Fetch Book
        Book book = bookRepository.findByIsbn(dto.getBook_isbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with isbn " + dto.getBook_isbn()));

        if (!book.isAvailable()) {
            throw new BadRequestException("Book with isbn " + dto.getBook_isbn() + " is currently unavailable");
        }

        BigDecimal bookPrice = book.getPrice();

        // Fetch Borrower
        Borrower borrower = borrowerRepository.findById(dto.getBorrower_id())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id " + dto.getBorrower_id()));

        if (borrower.getCardNumber() == null || borrower.getCardNumber().isEmpty()) {
            throw new BadRequestException("Borrower does not have a card number assigned");
        }

        // Call cms
        long borrowedDays = ChronoUnit.DAYS.between(dto.getBorrowDate(), dto.getReturnDate()); // e.g., 10 days
        BigDecimal basePrice = book.getPrice();
        BigDecimal insuranceFees = book.getInsurance_fees();
        BigDecimal extraDayPrice = book.getExtra_days_rental_price();

        long extraDays = Math.max(0, borrowedDays - 7); // only days beyond 1 week
        BigDecimal totalExtra = extraDayPrice.multiply(BigDecimal.valueOf(extraDays));

        BigDecimal totalAmount = basePrice.add(totalExtra).add(insuranceFees);

        CreateTransactionRequest cmsRequest = new CreateTransactionRequest();
        cmsRequest.setTransactionAmount(totalAmount);
        cmsRequest.setTransactionType(Type.D);
        cmsRequest.setCardNumber(borrower.getCardNumber());
        TransactionResponse cmsResponse;
        try {
            cmsResponse = cmsClient.createTransaction(borrower.getCardNumber(), cmsRequest);
            logger.info("CMS transaction created with ID {}", cmsResponse.getId());
        } catch (Exception e) {
            throw new BadRequestException("Transaction failed: " + e.getMessage());
        }
        // Map DTO to BorrowingTransaction entity
        BorrowingTransaction transaction = modelMapper.map(dto, BorrowingTransaction.class);
        transaction.setBook(book);
        transaction.setBorrower(borrower);
        transaction.setStatus(BorrowingStatus.BORROWED);
        transaction.setBook_Price(totalAmount);

        // Mark book as unavailable
        book.setAvailable(false);
        bookRepository.save(book);

        // Save BorrowingTransaction
        BorrowingTransaction savedTransaction = transactionRepository.saveAndFlush(transaction);

        // commit happens here if method ends successfully
        transactionRepository.flush();

        String message = "Dear " + borrower.getName() +
                ", Book \"" + book.getTitle() + "\" borrowed successfully. " +
                "Total charged = " + totalAmount + "$. " +
                "Your new account balance = " + cmsResponse.getBalance() + "$. " +
                "The base price is : "+basePrice+" $, " +
                "the insurance fees is:"+insuranceFees+" $, " +
                "the days that you borrowed: "+borrowedDays+", " +
                "in each day we have fees: " +extraDayPrice+" $.";
        try {
            emailServiceClient.sendEmail(new EmailRequest(borrower.getEmail(), message));
        } catch (Exception ex) {
            logger.error("Email sending failed, but transaction committed", ex);
        }


        return savedTransaction;
    }



    @Transactional
    public BorrowingTransaction returnBook(UUID transactionId) {
        // Fetch transaction
        BorrowingTransaction transaction = transactionRepository.findById(transactionId);

        // Check if already returned
        if (transaction.getStatus() == BorrowingStatus.RETURNED) {
            throw new BadRequestException("Book has already been returned for transaction id " + transactionId);
        }

        // Set return date and status
        transaction.setReturnDate(LocalDate.now());
        transaction.setStatus(BorrowingStatus.RETURNED);

        // Mark book as available
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        // Refund insurance fee if returned on or before due date
        BigDecimal refundedAmount = BigDecimal.ZERO;
        boolean refunded = false;

        if (!transaction.getReturnDate().isAfter(transaction.getReturnDate())) { // On or before due date
            BigDecimal insuranceFee = book.getInsurance_fees();
            refundedAmount = insuranceFee;

            CreateTransactionRequest cmsRequest = new CreateTransactionRequest();
            cmsRequest.setTransactionAmount(insuranceFee);
            cmsRequest.setTransactionType(Type.C); // Credit
            cmsRequest.setCardNumber(transaction.getBorrower().getCardNumber());

            try {
                cmsClient.createTransaction(transaction.getBorrower().getCardNumber(), cmsRequest);
                refunded = true;
                logger.info("Refunded insurance fee {} for transaction {}", insuranceFee, transactionId);
            } catch (Exception e) {
                logger.error("Failed to refund insurance fee for transaction {}: {}", transactionId, e.getMessage());
            }
        }

        // Save updated transaction
        BorrowingTransaction updatedTransaction = transactionRepository.save(transaction);

        // Send email notification
        String message = "Dear " + transaction.getBorrower().getName() +
                ", Book \"" + book.getTitle() + "\" returned successfully. " +
                "Insurance fee refunded: " + (refunded ? refundedAmount : BigDecimal.ZERO) + " $";
        try {
            emailServiceClient.sendEmail(new EmailRequest(transaction.getBorrower().getEmail(), message));
        } catch (Exception ex) {
            logger.error("Email sending failed for transaction {}: {}", transactionId, ex.getMessage());
        }

        return updatedTransaction;
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
