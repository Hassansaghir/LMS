
package com.LMS.library_management.Service;

import com.LMS.library_management.Dto.CreateTransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.LMS.library_management.Dto.TransactionResponse;

@FeignClient(name = "card-management-system", url = "http://localhost:9090")
public interface CmsClient {

    @PostMapping("/transactions/{cardNumber}")
    TransactionResponse createTransaction(
            @PathVariable("cardNumber") String cardNumber,
            @RequestBody CreateTransactionRequest request
    );
}


