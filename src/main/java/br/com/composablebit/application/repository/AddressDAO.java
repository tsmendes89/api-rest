package br.com.composablebit.application.repository;

import br.com.composablebit.application.domain.Address;
import br.com.composablebit.application.domain.Customer;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;
import java.util.List;

public interface AddressDAO {

    Address insert(Address address, Jdbi jdbi);
    List<Address> select(HashMap<String, String> filters, Jdbi jdbi);
    void update(Address address, Jdbi jdbi);
    void delete(Address address, Jdbi jdbi);

}
