package com.LMS.Service;

import com.LMS.Dto.AuthorDTO;
import com.LMS.Models.Author;
import com.LMS.Repository.AuthorRepository;
import com.LMS.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ModelMapper modelMapper;

    private Author author;
    private AuthorDTO authorDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorDTO = new AuthorDTO();
        authorDTO.setName("John Doe");
        authorDTO.setBiography("Bio");

        author = new Author();
        author.setId(UUID.randomUUID());
        author.setName("John Doe");
        author.setBiography("Bio");
    }

    @Test
    void testCreateAuthor() {
        when(modelMapper.map(authorDTO, Author.class)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.createAuthor(authorDTO);
        assertNotNull(result);
        assertEquals(author.getName(), result.getName());
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testGetAuthorById_NotFound() {
        UUID id = UUID.randomUUID();
        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authorService.getAuthorById(id));
    }
}
