package com.LMS.Controller;

import com.LMS.Dto.BorrowerDTO;
import com.LMS.Dto.CreateBorrower;
import com.LMS.Dto.CustomResponse;
import com.LMS.Models.Borrower;
import com.LMS.Service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;
    private final Maplist maplist;
    private static final Logger logger = LoggerFactory.getLogger(BorrowerController.class);
    private final ModelMapper modelMapper;

    @PostMapping
    public CustomResponse<BorrowerDTO> createBorrower(@Valid @RequestBody CreateBorrower dto) {
        try {
            Borrower created = borrowerService.createBorrower(dto);
            logger.info("Borrower created with id {}", created.getId());
            BorrowerDTO borrowerDTO = modelMapper.map(created, BorrowerDTO.class);
            return new CustomResponse<>("success", "Borrower created successfully", borrowerDTO);
        } catch (Exception e) {
            logger.error("Error creating borrower: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to create borrower", null);
        }
    }

    @PatchMapping("/{id}")
    public CustomResponse<BorrowerDTO> updateBorrower(@PathVariable UUID id, @Valid @RequestBody CreateBorrower dto) {
        try {
            Borrower updated = borrowerService.patchBorrower(id, dto);
            logger.info("Borrower updated with id {}", updated.getId());
            BorrowerDTO borrowerDTO = modelMapper.map(updated, BorrowerDTO.class);
            return new CustomResponse<>("success", "Borrower updated successfully", borrowerDTO);
        } catch (Exception e) {
            logger.error("Error updating borrower: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to update borrower", null);
        }
    }

    @GetMapping("/{id}")
    public CustomResponse<BorrowerDTO> getBorrowerById(@PathVariable UUID id) {
        try {
            Borrower borrower = borrowerService.getBorrowerById(id);
            if (borrower == null) {
                return new CustomResponse<>("error", "Borrower not found", null);
            }
            BorrowerDTO borrowerDTO = modelMapper.map(borrower, BorrowerDTO.class);
            return new CustomResponse<>("success", "Borrower retrieved successfully", borrowerDTO);
        } catch (Exception e) {
            logger.error("Error retrieving borrower: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve borrower", null);
        }
    }

    @GetMapping
    public CustomResponse<List<BorrowerDTO>> getAllBorrowers() {
        try {
            List<Borrower> borrowers = borrowerService.getAllBorrowers();
            List<BorrowerDTO> borrowerDTOs = maplist.mapList(borrowers, BorrowerDTO.class);
            return new CustomResponse<>("success", "Borrowers retrieved successfully", borrowerDTOs);
        } catch (Exception e) {
            logger.error("Error retrieving borrowers: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to retrieve borrowers", null);
        }
    }

    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteBorrower(@PathVariable UUID id) {
        try {
            borrowerService.deleteBorrower(id);
            logger.info("Borrower deleted with id {}", id);
            return new CustomResponse<>("success", "Borrower deleted successfully", null);
        } catch (Exception e) {
            logger.error("Error deleting borrower: {}", e.getMessage());
            return new CustomResponse<>("error", "Failed to delete borrower", null);
        }
    }
}
