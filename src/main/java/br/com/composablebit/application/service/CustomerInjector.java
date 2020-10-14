package br.com.composablebit.application.service;

import br.com.composablebit.application.repository.AddressDAO;
import br.com.composablebit.application.repository.CustomerDAO;
import br.com.composablebit.application.repository.impl.AddressDAOImpl;
import br.com.composablebit.application.repository.impl.CustomerDAOImpl;
import br.com.composablebit.application.service.database.Connection;
import br.com.composablebit.application.service.database.impl.ConnectionImpl;
import br.com.composablebit.application.service.impl.CustomerServiceImpl;
import com.google.inject.AbstractModule;

public class CustomerInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(CustomerService.class).to(CustomerServiceImpl.class);
        bind(CustomerDAO.class).to(CustomerDAOImpl.class);
        bind(Connection.class).to(ConnectionImpl.class);
        bind(AddressDAO.class).to(AddressDAOImpl.class);
    }

}
