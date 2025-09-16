package com.LMS.Controller;

import com.LMS.Dto.BookDTO;
import com.LMS.Dto.BookResponse;
import com.LMS.Dto.CreateBook;
import com.LMS.Dto.UpdateBook;
import com.LMS.Models.Book;
import com.LMS.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private  ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBook dto) {
        BookResponse response = bookService.createBook(dto);
        return ResponseEntity.ok(response);
    }




    @PatchMapping("/{isbn}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable String isbn, @Valid @RequestBody UpdateBook dto) {
        Book updated = bookService.updateBook(isbn, dto);
        logger.info("Author updated with id {}", updated.getId());
        return ResponseEntity.ok(modelMapper.map(updated, BookDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable UUID id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(modelMapper.map(book, BookDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTO=maplist.mapList(books, BookDTO.class);
        for (int i=0;i<books.size();i++){
            bookDTO.get(i).setAuthurName(books.get(i).getAuthor().getName());
        }
        return ResponseEntity.ok(bookDTO);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        logger.info("Book deleted with isbn {}", isbn);
        return ResponseEntity.noContent().build();
    }
}
