
package com.LMS.Service;

import com.LMS.Dto.CreateTransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.LMS.Dto.TransactionResponse;

@FeignClient(name = "card-management-system", url = "http://localhost:9090")
public interface CmsClient {

    @PostMapping("/v1/transactions/{cardNumber}")
    TransactionResponse createTransaction(
            @PathVariable("cardNumber") String cardNumber,
            @RequestBody CreateTransactionRequest request
    );
}


