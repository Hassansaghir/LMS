package com.LMS.Controller;

import com.LMS.Dto.GetAuthorDto;
import com.LMS.Dto.AuthorDTO;
import com.LMS.Models.Author;
import com.LMS.Service.AuthorService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final Maplist maplist;
    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);
    @Autowired
    private  ModelMapper modelMapper;
    public AuthorController(AuthorService authorService, Maplist maplist) {
        this.authorService = authorService;
        this.maplist = maplist;
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO  dto) {//HYDE BT2ARER SHU BKON BL PARAMETER
        Author created = authorService.createAuthor(dto);
        logger.info("Author created with id {}", created.getId());
        return ResponseEntity.ok(modelMapper.map(created, AuthorDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable UUID id, @Valid @RequestBody AuthorDTO dto) {
        Author updated = authorService.updateAuthor(id, dto);
        logger.info("Author updated with id {}", updated.getId());
        return ResponseEntity.ok(modelMapper.map(updated, AuthorDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable UUID id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(modelMapper.map(author, AuthorDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<GetAuthorDto>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        List<GetAuthorDto> authorDto =maplist.mapList(authors,GetAuthorDto.class);
        return ResponseEntity.ok(authorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID id) {
        authorService.deleteAuthor(id);
        logger.info("Author deleted with id {}", id);
        return ResponseEntity.noContent().build();
    }
}
