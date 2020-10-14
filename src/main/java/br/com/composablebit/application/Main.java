package br.com.composablebit.application;

import br.com.composablebit.application.domain.Address;
import br.com.composablebit.application.service.CustomerInjector;
import br.com.composablebit.application.service.database.impl.ConnectionImpl;
import br.com.composablebit.application.service.impl.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import br.com.composablebit.application.domain.Customer;
import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new CustomerInjector());
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerServiceImpl customerServiceImpl = injector.getInstance(CustomerServiceImpl.class);
        ConnectionImpl connectionImpl = injector.getInstance(ConnectionImpl.class);
        Jdbi jdbi = connectionImpl.connectToDatabase();
        //port(8080);

        post("/customers", (req, res) -> {
            try {
                res.type("application/json");
                Customer customer = objectMapper.readValue(req.body(), Customer.class);
                final String jsonRetorno = customerServiceImpl.createCustomer(customer, injector, jdbi);
                res.status(201);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(400);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        get("/customers", (req, res) -> {
            try {
                res.type("application/json");
                final String jsonRetorno = customerServiceImpl.listCustomers(req.body(), injector, jdbi);
                res.status(200);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(204);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        get("/customers/:id", (req, res) -> {
            try {
                res.type("application/json");
                final String jsonRetorno = customerServiceImpl.getCustomerById(req.params(":id"), injector, jdbi);
                res.status(200);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(404);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        put("/customers/:id", (req, res) -> {
            try {
                res.type("application/json");
                Customer customer = objectMapper.readValue(req.body(), Customer.class);
                customer.setId(Integer.parseInt(req.params(":id")));
                final String jsonRetorno = customerServiceImpl.updateCustomer(customer, injector, jdbi);
                res.status(200);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(404);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return e.getMessage();
            }
        });

        delete("/customers/:id", (req, res) -> {
            try {
                res.type("application/json");
                customerServiceImpl.deleteCustomer(req.params(":id"), injector, jdbi);
                res.status(204);
                return "";
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        post("/customers/:id/adresses", (req, res) -> {
            try {
                res.type("application/json");
                Address address = objectMapper.readValue(req.body(), Address.class);
                address.setCustomer_id(Integer.parseInt(req.params(":id")));
                final String jsonRetorno = customerServiceImpl.createAddress(address, injector, jdbi);
                res.status(201);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(400);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        get("/customers/:id/adresses", (req, res) -> {
            try {
                res.type("application/json");
                HashMap<String, String> filters = new HashMap<String, String>();
                filters.put("customer_id", req.params(":id"));
                final String jsonRetorno = customerServiceImpl.listAddresses(filters, injector, jdbi);
                res.status(200);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(204);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        get("/customers/:id/adresses/:address_id", (req, res) -> {
            try {
                res.type("application/json");
                HashMap<String, String> filters = new HashMap<String, String>();
                filters.put("customer_id", req.params(":id"));
                filters.put("id", req.params(":address_id"));
                final String jsonRetorno = customerServiceImpl.listAddresses(filters, injector, jdbi);
                res.status(200);
                return jsonRetorno;
            } catch (IllegalArgumentException e) {
                res.status(404);
                return e.getMessage();
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        put("/customers/:id/adresses/:address_id", (req, res) -> {
            try {
                res.type("application/json");
                HashMap<String, String> filters = new HashMap<String, String>();
                filters.put("customer_id", req.params(":id"));
                filters.put("id", req.params(":address_id"));
                Address address = objectMapper.readValue(req.body(), Address.class);
                final String jsonRetorno = customerServiceImpl.updateAddresss(filters, address, injector, jdbi);
                res.status(200);
                return jsonRetorno;
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

        delete("/customers/:id/adresses/:address_id", (req, res) -> {
            try {
                res.type("application/json");
                customerServiceImpl.deleteAddress(req.params(":address_id"), injector, jdbi);
                res.status(200);
                return "Address removido com sucesso";
            } catch (Exception e) {
                res.status(500);
                return customerServiceImpl.getJsonException("");
            }
        });

    }
}
