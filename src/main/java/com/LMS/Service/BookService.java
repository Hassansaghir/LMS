// service/BookService.java
package com.LMS.Service;
import com.LMS.Dto.BookResponse;
import com.LMS.Dto.CreateBook;
import com.LMS.Dto.UpdateBook;
import com.LMS.Repository.BorrowingTransactionRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import com.LMS.Models.Author;
import com.LMS.Models.Book;
import com.LMS.Models.Category;
import com.LMS.Exception.ResourceNotFoundException;
import com.LMS.Repository.AuthorRepository;
import com.LMS.Repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class BookService {
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final OpenLibraryClient openLibraryClient;
    private  final  BorrowingTransactionRepository borrowingTransactionRepository;
    public BookService(ModelMapper modelMapper, BookRepository bookRepository, AuthorRepository authorRepository, OpenLibraryClient openLibraryClient, BorrowingTransactionRepository borrowingTransactionRepository) {
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.openLibraryClient = openLibraryClient;
        this.borrowingTransactionRepository = borrowingTransactionRepository;
    }


    public BookResponse createBook(CreateBook dto) {
        OpenLibraryClient.AuthorData authorData = openLibraryClient.fetchAuthorByIsbn(dto.getIsbn());
        Author author;

        if (authorData != null) {
            author = authorRepository.findByName(authorData.getName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        newAuthor.setName(authorData.getName());
                        newAuthor.setOpenLibraryUrl(authorData.getUrl()); // Optional: store URL in DB
                        return authorRepository.save(newAuthor);
                    });
        } else {
            author = authorRepository.findByName("Unknown Author")
                    .orElseGet(() -> {
                        Author unknown = new Author();
                        unknown.setName("Unknown Author");
                        unknown.setOpenLibraryUrl(""); // empty URL
                        return authorRepository.save(unknown);
                    });
        }

        // Map DTO → Entity
        Book book = modelMapper.map(dto, Book.class);
        book.setAuthor(author);
        Book savedBook = bookRepository.save(book);
        // Build response
        BookResponse.BookInfo info = new BookResponse.BookInfo();
        info.setTitle(savedBook.getTitle());

        BookResponse.AuthorInfo authorInfo = new BookResponse.AuthorInfo();
        authorInfo.setName(savedBook.getAuthor().getName());
        authorInfo.setUrl(savedBook.getAuthor().getOpenLibraryUrl()); // dynamic URL

        info.setAuthors(List.of(authorInfo));

        BookResponse response = new BookResponse();
        response.setBooks(Map.of("ISBN:" + savedBook.getIsbn(), info));

        return response;
    }










    public Book updateBook(String isbn, UpdateBook dto) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + isbn));


        modelMapper.map(dto,book);

        Book updated = bookRepository.save(book);
        logger.info("Updated book with id {}", updated.getId());
        return updated;
    }


    public Book getBookById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return book;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    @Transactional
    public void deleteBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with isbn " + isbn));

        // Check for existing borrowing transactions
        if (!borrowingTransactionRepository.findByBookId(book.getId()).isEmpty()) {
            throw new IllegalStateException("Cannot delete book; it has borrowing transactions.");
        }

        bookRepository.delete(book);
        logger.info("Deleted book with isbn {}", isbn);
    }



    public List<Book> searchBooks(String title, Category category, UUID authorId) {
        if (title != null && !title.isBlank()) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (category != null) {
            return bookRepository.findByCategory(category);
        } else if (authorId != null) {
            return bookRepository.findByAuthorId(authorId);
        } else {
            return getAllBooks();
        }
    }

    public List<Book> getBooksByAuthor(UUID authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    public void updateBookAvailability(UUID bookId, boolean available) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));
        book.setAvailable(available);
        bookRepository.save(book);
        logger.info("Updated availability of book id {} to {}", bookId, available);
    }
}
