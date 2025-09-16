package com.LMS.Controller;

import com.LMS.Dto.CustomResponse;
import com.LMS.Dto.GetAuthorDto;
import com.LMS.Dto.AuthorDTO;
import com.LMS.Models.Author;
import com.LMS.Service.AuthorService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ModelMapper modelMapper;

    public AuthorController(AuthorService authorService, Maplist maplist) {
        this.authorService = authorService;
        this.maplist = maplist;
    }

    @PostMapping
    public CustomResponse<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO dto) {
        try {
            Author created = authorService.createAuthor(dto);
            logger.info("Author created with id {}", created.getId());
            AuthorDTO createdDTO = modelMapper.map(created, AuthorDTO.class);
            return new CustomResponse<>("success", "Author created successfully", createdDTO);
        } catch (Exception e) {
            logger.error("Error creating author: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to create author", null);
        }
    }

    @PutMapping("/{id}")
    public CustomResponse<AuthorDTO> updateAuthor(@PathVariable UUID id, @Valid @RequestBody AuthorDTO dto) {
        try {
            Author updated = authorService.updateAuthor(id, dto);
            logger.info("Author updated with id {}", updated.getId());
            AuthorDTO authorDTO = modelMapper.map(updated, AuthorDTO.class);
            return new CustomResponse<>("success", "Author updated successfully", authorDTO);
        } catch (Exception e) {
            logger.error("Error updating author: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to update author", null);
        }
    }

    @GetMapping("/{id}")
    public CustomResponse<AuthorDTO> getAuthorById(@PathVariable UUID id) {
        try {
            Author author = authorService.getAuthorById(id);
            if (author == null) {
                return new CustomResponse<>("error", "Author not found", null);
            }
            AuthorDTO authorDTO = modelMapper.map(author, AuthorDTO.class);
            return new CustomResponse<>("success", "Author retrieved successfully", authorDTO);
        } catch (Exception e) {
            logger.error("Error retrieving author: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve author", null);
        }
    }

    @GetMapping
    public CustomResponse<List<GetAuthorDto>> getAllAuthors() {
        try {
            List<Author> authors = authorService.getAllAuthors();
            List<GetAuthorDto> authorDto = maplist.mapList(authors, GetAuthorDto.class);
            return new CustomResponse<>("success", "Authors retrieved successfully", authorDto);
        } catch (Exception e) {
            logger.error("Error retrieving authors: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve authors", null);
        }
    }

    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteAuthor(@PathVariable UUID id) {
        try {
            authorService.deleteAuthor(id);
            logger.info("Author deleted with id {}", id);
            return new CustomResponse<>("success", "Author deleted successfully", null);
        } catch (Exception e) {
            logger.error("Error deleting author: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to delete author", null);
        }
    }
}
