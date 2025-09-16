package com.LMS.Config;
import com.LMS.Dto.BorrowingTransactionDTO;
import com.LMS.Models.BorrowingTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        // Allow Swagger URLs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Allow all other requests (optional)
                        .anyRequest().permitAll()
                );

        return http.build();
    }
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);

        // Explicitly skip id
        mapper.typeMap(BorrowingTransactionDTO.class, BorrowingTransaction.class)
                .addMappings(m -> {
                    m.skip(BorrowingTransaction::setId);
                });


        return mapper;
    }



}
