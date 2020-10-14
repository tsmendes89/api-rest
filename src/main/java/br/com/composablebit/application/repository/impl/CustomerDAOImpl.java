package br.com.composablebit.application.repository.impl;

import br.com.composablebit.application.domain.Address;
import br.com.composablebit.application.repository.CustomerDAO;
import br.com.composablebit.application.domain.Customer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public void delete(Customer customer, Jdbi jdbi) {

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Customer.class));
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            handle.createUpdate(" delete from address " +
                    " where customer_id = :id")
                    .bind("id", customer.getId())
                    .execute();

            handle.createUpdate(" delete from customer " +
                    " where id = :id")
                    .bind("id", customer.getId())
                    .execute();

        });

    }

    @Override
    public Customer insert(Customer customer, Jdbi jdbi) {

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Customer.class));
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            handle.createUpdate("INSERT INTO customer(name, email, birthdate, cpf, gender, created_at, update_at) VALUES (:name, :email, :birthDate, :cpf, :gender, :createdAt, :updateAt)")
                    .bindBean(customer)
                    .execute();

            int key = handle.createQuery("select id from customer where cpf = :cpf")
                    .bind("cpf", customer.getCpf())
                    .mapTo(Integer.class)
                    .findOnly();

            customer.setId(key);

        });

        return customer;

    }

    @Override
    public void update(Customer customer, Jdbi jdbi) {

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Customer.class));
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            /* TODO atualizar o campo update_at*/
            handle.createUpdate(" update customer " +
                    " set name = coalesce(:name, name), " +
                    "    email = coalesce(:email, email), " +
                    "    birthdate = coalesce(:birthDate, birthdate), " +
                    "    cpf = coalesce(:cpf, cpf), " +
                    "    gender = coalesce(:gender, gender), " +
                    "    update_at = coalesce(:updateAt, update_at) " +
                    " where id = :id")
                    .bind("id", customer.getId())
                    .bind("name", customer.getName())
                    .bind("email", customer.getEmail())
                    .bind("birthDate", customer.getBirthDate())
                    .bind("cpf", customer.getCpf())
                    .bind("gender", customer.getGender())
                    .bind("updateAt", customer.getUpdateAt())
                    .execute();

        });
    }

    public List<Customer> select(HashMap<String, String> filters, Jdbi jdbi){

        List<Customer> customersList = new ArrayList<Customer>();

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Customer.class));

            List<Customer>customers = handle.createQuery("" +
                    " select c.birthdate, " +
                    "        insert(insert(insert(lpad(c.cpf, 11, '0'),10,0,'-'),7,0,'.'),4,0,'.' ) as cpf, " +
                    "        c.created_at, " +
                    "        c.email, " +
                    "        c.gender, " +
                    "        c.id, " +
                    "        c.name, " +
                    "        c.update_at " +
                    " from customer c " +
                    " where (:name is null or c.name = :name) " +
                    " and (:id is null or cast(c.id as char) = :id) " +
                    " and (:cpf is null or cast(c.cpf as char) = :cpf) " +
                    " and (:birthdate is null or date_format(c.birthdate, '%Y-%m-%d') = :birthdate) " +
                    " and exists ( " +
                    "  select 0 " +
                    "  from address a " +
                    "  where a.customer_id = c.id " +
                    "  and (:state is null or a.state = :state)" +
                    "  and (:city is null or a.city = :city)" +
                    " order by :sortBy) ")
                    .bind("name", filters.get("name"))
                    .bind("birthdate", filters.get("birthDate"))
                    .bind("state", filters.get("state"))
                    .bind("city", filters.get("city"))
                    .bind("id", filters.get("id"))
                    .bind("cpf", filters.get("cpf"))
                    .bind("sortBy", filters.get("name"))
                    .mapTo(Customer.class)
                    .list();

            customersList.addAll(customers);

        });

        return customersList;
    }
}
