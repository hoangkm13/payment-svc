package com.example.paymentservice.controller;

import com.example.paymentservice.controller.request.auth.*;
import com.example.paymentservice.entities.Customer;
import com.example.paymentservice.exception.CustomException;
import com.example.paymentservice.model.ApiResponse;
import com.example.paymentservice.service.CustomerService;
import com.example.paymentservice.util.TokenUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class CustomerController {
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;


    @Autowired
    public CustomerController(AuthenticationManager authenticationManager, TokenUtils tokenUtils, CustomerService customerService, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.customerService = customerService;
        this.modelMapper = modelMapper;
    }


    @PostMapping(value = "/login", produces = "application/json")
    public ApiResponse<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) throws CustomException {
        var user = customerService.findByUsername(loginRequestDTO.getUsername());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword())
        );

        String token = tokenUtils.generateToken(authentication);
        LoginResponseDTO userToken = new LoginResponseDTO(token, user);
        return ApiResponse.successWithResult(userToken);
    }

    @GetMapping(produces = "application/json")
    public ApiResponse<CustomerDTO> getCurrentUser() throws CustomException {
        Customer authentication = (Customer) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        Customer customer = customerService.findByUsername(authentication.getUsername());
        return ApiResponse.successWithResult(modelMapper.map(customer, CustomerDTO.class));
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ApiResponse<CustomerDTO> register(@Valid @RequestBody CustomerDTO customerDTO) throws CustomException {
        var customer = customerService.createUser(customerDTO);
        return ApiResponse.successWithResult(modelMapper.map(customer, CustomerDTO.class));
    }

    @PutMapping(value = "{customerId}", produces = "application/json")
    public ApiResponse<CustomerDTO> updateUser(@PathVariable String customerId, @Valid @RequestBody UpdateCustomerDTO customerDTO) throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var customer = customerService.preCheckUpdateUserInfo(customerDTO, authentication.getPrincipal().toString(), customerId);
        return ApiResponse.successWithResult(modelMapper.map(customer, CustomerDTO.class));
    }

    @PostMapping(value = "changePassword/{customerId}", produces = "application/json")
    public ApiResponse<CustomerDTO> resetPassword(@PathVariable String customerId, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var customer = customerService.resetPassword(resetPasswordDTO, String.valueOf(authentication.getPrincipal().toString()), customerId);
        return ApiResponse.successWithResult(modelMapper.map(customer, CustomerDTO.class));
    }
}
