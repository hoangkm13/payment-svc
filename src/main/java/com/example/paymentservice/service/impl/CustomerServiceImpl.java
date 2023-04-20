package com.example.paymentservice.service.impl;

import com.example.paymentservice.constants.ErrorCode;
import com.example.paymentservice.controller.request.auth.ResetPasswordDTO;
import com.example.paymentservice.controller.request.auth.UpdateCustomerDTO;
import com.example.paymentservice.controller.request.auth.CustomerDTO;
import com.example.paymentservice.entities.Customer;
import com.example.paymentservice.entities.PaymentTransaction;
import com.example.paymentservice.enums.Role;
import com.example.paymentservice.exception.CustomException;
import com.example.paymentservice.repository.CustomerRepository;
import com.example.paymentservice.service.CustomerService;
import com.example.paymentservice.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService, UserDetailsService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, AuthUtils authUtils) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authUtils = authUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Customer> optionalUser = customerRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        Customer customer = optionalUser.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(customer.getRole());
        authorities.add(authority);
        return new org.springframework.security.core.userdetails.User(customer.getUsername(), customer.getPasswordHash(), authorities);
    }

    @Override
    public Customer findByUsername(String username) throws CustomException {
        Optional<Customer> optionalUser = customerRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.CUSTOMER_NOT_EXIST);
        }
        return optionalUser.get();
    }

    @Override
    public Customer createUser(CustomerDTO customerDTO) throws CustomException {
        if (customerRepository.existsByUsername(customerDTO.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_EXIST);
        }
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_EXIST);
        }

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setGender(customerDTO.getGender());
        customer.setBirthOfDate(customerDTO.getBirthOfDate());
        customer.setMobile(customerDTO.getMobile());
        customer.setRole(Role.USER.getValue());
        customer.setUsername(customerDTO.getUsername());
        customer.setEmail(customerDTO.getEmail());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setPasswordHash(passwordEncoder.encode(customerDTO.getPassword()));
        return customerRepository.save(customer);
    }

    @Override
    public void checkPermission(String userId) throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer currentCustomer = findById(authentication.getName());
        if (!userId.equals(currentCustomer.getId()) && !authUtils.isAdmin()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public Customer findById(String customerId) throws CustomException {
        var user = this.customerRepository.findById(customerId);
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.CUSTOMER_NOT_EXIST);
        }

        return user.get();
    }

    @Override
    public Customer preCheckUpdateUserInfo(UpdateCustomerDTO updateCustomerDTO, String currentCustomerId, String customerId) throws CustomException {
        var existedUser = this.findById(currentCustomerId);

        if (!Objects.equals(customerId, existedUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return this.updateCustomer(updateCustomerDTO, existedUser);
    }

    @Override
    public Customer resetPassword(ResetPasswordDTO resetPasswordDTO, String currentUserId, String userId) throws CustomException {
        var existedUser = this.findById(currentUserId);

        if (!Objects.equals(userId, existedUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        existedUser.setPasswordHash(passwordEncoder.encode(resetPasswordDTO.getPassword()));

        return this.customerRepository.save(existedUser);
    }

    @Override
    public void addPaymentTransaction(PaymentTransaction paymentTransaction) throws CustomException {
        var customer = findById(paymentTransaction.getCustomerId());

        if (customer.getListRepaymentTransactionIds() == null) {
            List<String> list = new ArrayList<>();
            customer.setListRepaymentTransactionIds(list);
        }
        customer.getListRepaymentTransactionIds().add(paymentTransaction.getId());
        this.customerRepository.save(customer);
    }

    private Customer updateCustomer(UpdateCustomerDTO updateCustomerDTO, Customer existedCustomer) {

        existedCustomer.setGender(updateCustomerDTO.getGender() != null ? updateCustomerDTO.getGender() : existedCustomer.getGender());
        existedCustomer.setBirthOfDate(updateCustomerDTO.getBirthOfDate() != null ? updateCustomerDTO.getBirthOfDate() : existedCustomer.getBirthOfDate());
        existedCustomer.setMobile(updateCustomerDTO.getMobile() != null ? updateCustomerDTO.getMobile() : existedCustomer.getMobile());
        existedCustomer.setEmail(updateCustomerDTO.getEmail() != null ? updateCustomerDTO.getEmail() : existedCustomer.getEmail());
        existedCustomer.setFirstName(updateCustomerDTO.getFirstName() != null ? updateCustomerDTO.getFirstName() : existedCustomer.getFirstName());
        existedCustomer.setLastName(updateCustomerDTO.getLastName() != null ? updateCustomerDTO.getLastName() : existedCustomer.getLastName());
        existedCustomer.setEmail(updateCustomerDTO.getEmail() != null ? updateCustomerDTO.getEmail() : existedCustomer.getEmail());

        this.customerRepository.save(existedCustomer);

        return existedCustomer;
    }
}
