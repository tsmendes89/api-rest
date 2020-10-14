package br.com.composablebit.application.repository.impl;

import br.com.composablebit.application.domain.Customer;
import br.com.composablebit.application.repository.AddressDAO;
import br.com.composablebit.application.domain.Address;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressDAOImpl implements AddressDAO {

    @Override
    public void delete(Address address, Jdbi jdbi) {

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            handle.createUpdate(" delete from address " +
                    " where id = :id")
                    .bind("id", address.getId())
                    .execute();

        });

    }

    @Override
    public Address insert(Address address, Jdbi jdbi) {

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            handle.createUpdate("INSERT INTO address(state, city, neighborhood, zipcode, street, number, additional_information, main, customer_id) VALUES (:state, :city, :neighborhood, :zipCode, :street, :number, :additionalInformation, :main, :customer_id)")
                    .bindBean(address)
                    .execute();

            int key = handle.createQuery("select max(id) from address where customer_id = :customer_id ")
                    .bind("customer_id", address.getCustomer_id())
                    .mapTo(Integer.class)
                    .findOnly();

            address.setId(key);

        });

        return address;

    }

    @Override
    public List<Address> select(HashMap<String, String> filters, Jdbi jdbi){

        List<Address> addressesList = new ArrayList<Address>();

        /*TODO PESQUISAR UMA FORMA DE TRAZER OS CUSTOMERS E ADDRESS PELO JDBI*/
        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            List<Address>addresses = handle.createQuery("" +
                    " select c.additional_information, " +
                    "        c.city, " +
                    "        c.customer_id, " +
                    "        c.id, " +
                    "        c.main, " +
                    "        c.neighborhood, " +
                    "        c.number, " +
                    "        c.state, " +
                    "        c.street, " +
                    "        insert(insert(lpad(c.zipcode,8,'0'), 6, 0, '-' ), 3, 0, '.' ) zipcode " +
                    " from address c " +
                    " where (:customer_id is null or c.customer_id = :customer_id) " +
                    " and (:id is null or c.id = :id) " +
                    " and (:main is null or c.main = :main) ")
                    .bind("customer_id", filters.get("customer_id"))
                    .bind("id", filters.get("id"))
                    .bind("main", filters.get("main"))
                    .mapTo(Address.class)
                    .list();

            addressesList.addAll(addresses);

        });

        return addressesList;
    }

    @Override
    public void update(Address address, Jdbi jdbi) {

        jdbi.useHandle(handle -> {
            handle.registerRowMapper(BeanMapper.factory(Address.class));

            /* TODO atualizar o campo update_at*/
            handle.createUpdate(" update address " +
                    " set state = coalesce(:state, state), " +
                    "    city = coalesce(:city, city), " +
                    "    neighborhood = coalesce(:neighborhood, neighborhood), " +
                    "    zipcode = coalesce(:zipCode, zipcode), " +
                    "    street = coalesce(:street, street), " +
                    "    number = coalesce(:number, number), " +
                    "    additional_information = coalesce(:additionalInformation, additional_information), " +
                    "    main = coalesce(:main, main) " +
                    " where id = :id")
                    .bind("id", address.getId())
                    .bind("state", address.getState())
                    .bind("city", address.getCity())
                    .bind("neighborhood", address.getNeighborhood())
                    .bind("zipCode", address.getZipCode())
                    .bind("street", address.getStreet())
                    .bind("number", address.getNumber())
                    .bind("additionalInformation", address.getAdditionalInformation())
                    .bind("main", address.isMain())
                    .execute();

        });
    }
}
