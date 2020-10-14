package br.com.composablebit.application.service;

import br.com.composablebit.application.domain.Address;
import com.google.inject.Injector;
import br.com.composablebit.application.domain.Customer;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;

public interface CustomerService {

    String createCustomer(Customer customer, Injector injector, Jdbi jdbi) throws Exception;
    String listCustomers(String json, Injector injector, Jdbi jdbi);
    String getCustomerById(String id, Injector injector, Jdbi jdbi);
    String updateCustomer(Customer customer, Injector injector, Jdbi jdbi);
    void deleteCustomer(String id, Injector injector, Jdbi jdbi);
    String createAddress(Address address, Injector injector, Jdbi jdbi);
    String listAddresses(HashMap<String, String> filters, Injector injector, Jdbi jdbi);
    String updateAddresss(HashMap<String, String> filters, Address address, Injector injector, Jdbi jdbi);
    void deleteAddress(String id, Injector injector, Jdbi jdbi);
    String getJsonException(String code);
}
