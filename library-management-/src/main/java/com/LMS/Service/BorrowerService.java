// service/BorrowerService.java
package com.LMS.Service;
import com.LMS.Dto.CreateBorrower;
import org.modelmapper.ModelMapper;
import com.LMS.Models.Borrower;
import com.LMS.Exception.ResourceNotFoundException;
import com.LMS.Repository.BorrowerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BorrowerService {
    private final ModelMapper modelMapper;
    private final BorrowerRepository borrowerRepository;
    private static final Logger logger = LoggerFactory.getLogger(BorrowerService.class);

    public BorrowerService(ModelMapper modelMapper, BorrowerRepository borrowerRepository) {
        this.modelMapper = modelMapper;
        this.borrowerRepository = borrowerRepository;
    }

    public Borrower createBorrower(CreateBorrower dto) {
        Borrower borrower = modelMapper.map(dto,Borrower.class);
        Borrower saved = borrowerRepository.save(borrower);
        logger.info("Created borrower with id {}", saved.getId());
        return saved;
    }

    public Borrower patchBorrower(UUID id, CreateBorrower dto) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id " + id));

        // This will map only non-null fields from dto to borrower and i put in config to skip NULL values
        modelMapper.map(dto, borrower);

        Borrower saved = borrowerRepository.save(borrower);
        logger.info("Updated borrower: {}", borrower.getName());
        return saved;
    }

    public Borrower getBorrowerById(UUID id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id " + id));
    }
    public List<Borrower> getAllBorrowers(){
        return  borrowerRepository.findAll();
    }
    public void deleteBorrower(UUID id){
        if (!borrowerRepository.existsById(id)) {
            throw new ResourceNotFoundException("borrower not found with id " + id);
        }
        borrowerRepository.deleteById(id);
        logger.info("Deleted book with id {}", id);
    }

}