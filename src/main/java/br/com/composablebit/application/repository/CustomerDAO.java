package br.com.composablebit.application.repository;

import br.com.composablebit.application.domain.Customer;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;
import java.util.List;

public interface CustomerDAO {

    Customer insert(Customer customer, Jdbi jdbi);

    List<Customer> select(HashMap<String, String> filters, Jdbi jdbi);

    void update(Customer customer, Jdbi jdbi);

    void delete(Customer customer, Jdbi jdbi);

}
