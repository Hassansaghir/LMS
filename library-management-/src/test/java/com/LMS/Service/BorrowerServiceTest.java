package com.LMS.Service;

import com.LMS.Dto.CreateBorrower;
import com.LMS.Exception.ResourceNotFoundException;
import com.LMS.Models.Borrower;
import com.LMS.Repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowerServiceTest {

    @InjectMocks
    private BorrowerService borrowerService;

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private ModelMapper modelMapper;

    private Borrower borrower;
    private CreateBorrower createBorrowerDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        createBorrowerDto = new CreateBorrower();
        createBorrowerDto.setName("Jane Doe");
        createBorrowerDto.setEmail("jane@example.com");

        borrower = new Borrower();
        borrower.setId(UUID.randomUUID());
        borrower.setName("Jane Doe");
        borrower.setEmail("jane@example.com");
    }

    @Test
    void testCreateBorrower() {
        when(modelMapper.map(createBorrowerDto, Borrower.class)).thenReturn(borrower);
        when(borrowerRepository.save(borrower)).thenReturn(borrower);

        Borrower result = borrowerService.createBorrower(createBorrowerDto);

        assertNotNull(result);
        assertEquals(borrower.getName(), result.getName());
        verify(borrowerRepository, times(1)).save(borrower);
    }

    @Test
    void testPatchBorrower_Success() {
        UUID id = borrower.getId();
        when(borrowerRepository.findById(id)).thenReturn(Optional.of(borrower));
        when(borrowerRepository.save(borrower)).thenReturn(borrower);

        Borrower result = borrowerService.patchBorrower(id, createBorrowerDto);

        assertNotNull(result);
        assertEquals(borrower.getName(), result.getName());
        verify(borrowerRepository, times(1)).save(borrower);
    }

    @Test
    void testPatchBorrower_NotFound() {
        UUID id = UUID.randomUUID();
        when(borrowerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> borrowerService.patchBorrower(id, createBorrowerDto));
    }

    @Test
    void testGetBorrowerById_Success() {
        UUID id = borrower.getId();
        when(borrowerRepository.findById(id)).thenReturn(Optional.of(borrower));

        Borrower result = borrowerService.getBorrowerById(id);

        assertNotNull(result);
        assertEquals(borrower.getName(), result.getName());
    }

    @Test
    void testGetBorrowerById_NotFound() {
        UUID id = UUID.randomUUID();
        when(borrowerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> borrowerService.getBorrowerById(id));
    }

    @Test
    void testGetAllBorrowers() {
        List<Borrower> list = Arrays.asList(borrower);
        when(borrowerRepository.findAll()).thenReturn(list);

        List<Borrower> result = borrowerService.getAllBorrowers();

        assertEquals(1, result.size());
        verify(borrowerRepository, times(1)).findAll();
    }

    @Test
    void testDeleteBorrower_Success() {
        UUID id = borrower.getId();
        when(borrowerRepository.existsById(id)).thenReturn(true);
        doNothing().when(borrowerRepository).deleteById(id);

        borrowerService.deleteBorrower(id);

        verify(borrowerRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteBorrower_NotFound() {
        UUID id = UUID.randomUUID();
        when(borrowerRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> borrowerService.deleteBorrower(id));
    }
}
