package com.LMS.Controller;

import com.LMS.Dto.BookDTO;
import com.LMS.Dto.BookResponse;
import com.LMS.Dto.CreateBook;
import com.LMS.Dto.UpdateBook;
import com.LMS.Dto.CustomResponse;
import com.LMS.Models.Book;
import com.LMS.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final Maplist maplist;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public CustomResponse<BookResponse> createBook(@RequestBody CreateBook dto) {
        try {
            BookResponse response = bookService.createBook(dto);
            return new CustomResponse<>("success", "Book created successfully", response);
        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to create book", null);
        }
    }

    @PatchMapping("/{isbn}")
    public CustomResponse<BookDTO> updateBook(@PathVariable String isbn, @Valid @RequestBody UpdateBook dto) {
        try {
            Book updated = bookService.updateBook(isbn, dto);
            logger.info("Book updated with id {}", updated.getId());
            BookDTO updatedDTO = modelMapper.map(updated, BookDTO.class);
            updatedDTO.setAuthurName(updated.getAuthor().getName());
            return new CustomResponse<>("success", "Book updated successfully", updatedDTO);
        } catch (Exception e) {
            logger.error("Error updating book: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to update book", null);
        }
    }

    @GetMapping("/{id}")
    public CustomResponse<BookDTO> getBookById(@PathVariable UUID id) {
        try {
            Book book = bookService.getBookById(id);
            if (book == null) {
                return new CustomResponse<>("error", "Book not found", null);
            }
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            bookDTO.setAuthurName(book.getAuthor().getName());
            return new CustomResponse<>("success", "Book retrieved successfully", bookDTO);
        } catch (Exception e) {
            logger.error("Error retrieving book: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve book", null);
        }
    }

    @GetMapping
    public CustomResponse<List<BookDTO>> getAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            List<BookDTO> bookDTO = maplist.mapList(books, BookDTO.class);
            for (int i = 0; i < books.size(); i++) {
                bookDTO.get(i).setAuthurName(books.get(i).getAuthor().getName());
            }
            return new CustomResponse<>("success", "Books retrieved successfully", bookDTO);
        } catch (Exception e) {
            logger.error("Error retrieving books: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve books", null);
        }
    }

    @DeleteMapping("/{isbn}")
    public CustomResponse<Void> deleteBook(@PathVariable String isbn) {
        try {
            bookService.deleteBook(isbn);
            logger.info("Book deleted with isbn {}", isbn);
            return new CustomResponse<>("success", "Book deleted successfully", null);
        } catch (Exception e) {
            logger.error("Error deleting book: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to delete book", null);
        }
    }
}
