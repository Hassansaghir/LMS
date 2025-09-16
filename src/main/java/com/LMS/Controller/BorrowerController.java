package com.LMS.Controller;

import com.LMS.Dto.BorrowerDTO;
import com.LMS.Dto.CreateBorrower;
import com.LMS.Models.Borrower;
import com.LMS.Service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
    private final ModelMapper modelmapper;

    @PostMapping
    public ResponseEntity<BorrowerDTO> createBorrower(@Valid @RequestBody CreateBorrower dto) {
        Borrower created = borrowerService.createBorrower(dto);
        logger.info("Borrower created with id {}", created.getId());
        return ResponseEntity.ok(modelmapper.map(created,BorrowerDTO.class));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BorrowerDTO> updateBorrower(@PathVariable UUID id, @Valid @RequestBody CreateBorrower dto) {
        Borrower updated = borrowerService.patchBorrower(id, dto);
        logger.info("Borrower updated with id {}", updated.getId());
        return ResponseEntity.ok(modelmapper.map(updated,BorrowerDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowerDTO> getBorrowerById(@PathVariable UUID id) {
        Borrower borrower = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(modelmapper.map(borrower,BorrowerDTO.class));


    }

    @GetMapping
    public ResponseEntity<List<BorrowerDTO>> getAllBorrowers() {
        List<Borrower> borrowers = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(maplist.mapList(borrowers,BorrowerDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrower(@PathVariable UUID id) {
        borrowerService.deleteBorrower(id);
        logger.info("Borrower deleted with id {}", id);
        return ResponseEntity.noContent().build();
    }
}
