package com.LMS.Service;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import com.LMS.Dto.AuthorDTO;
import com.LMS.Models.Author;
import com.LMS.Exception.ResourceNotFoundException;
import com.LMS.Repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    public Author createAuthor(AuthorDTO dto) {
        Author author = modelMapper.map(dto, Author.class);
        Author saved = authorRepository.save(author);
        logger.info("Created author with id {}", saved.getId());
        return saved;
    }


    public Author updateAuthor(UUID id, AuthorDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + id));
        modelMapper.map(dto,author);
        Author updated = authorRepository.save(author);
        logger.info("Updated author with id {}", updated.getId());
        return updated;
    }

    public Author getAuthorById(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + id));
        return author;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public void deleteAuthor(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id " + id);
        }
        authorRepository.deleteById(id);
        logger.info("Deleted author with id {}", id);
    }
}
