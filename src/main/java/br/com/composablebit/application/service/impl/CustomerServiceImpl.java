package br.com.composablebit.application.service.impl;

import br.com.composablebit.application.domain.Address;
import br.com.composablebit.application.repository.impl.AddressDAOImpl;
import br.com.composablebit.application.service.CustomerService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import br.com.composablebit.application.repository.impl.CustomerDAOImpl;
import br.com.composablebit.application.domain.Customer;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    @Override
    public void deleteCustomer(String id, Injector injector, Jdbi jdbi) {
        CustomerDAOImpl customerDAOImpl = injector.getInstance(CustomerDAOImpl.class);

        HashMap<String, String> filters = new HashMap<String, String>();
        String customedId = id;
        filters.put("id", customedId);
        List<Customer> customersList = customerDAOImpl.select(filters, jdbi);
        customerDAOImpl.delete(customersList.get(0), jdbi);
    }

    @Override
    public void deleteAddress(String id, Injector injector, Jdbi jdbi) {
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);
        Address address = new Address();
        address.setId(Integer.parseInt(id));
        addressDAOImpl.delete(address, jdbi);
    }

    @Override
    public String createCustomer(Customer customer, Injector injector, Jdbi jdbi)  {

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        Date dataAniversario = null;

        try {
            dataAniversario = formataData(customer.getBirthDate());
        }catch (Exception e){
            e.printStackTrace();
        }

        if (dataAniversario != null && getIdade(dataAniversario) > 100) {
            throw new IllegalArgumentException(getJsonException("ageCheck"));
        }

        if (customer.getName() == null){
            throw new IllegalArgumentException(getJsonException("createCustomer"));
        }

        // retirando os caracteres especiais do cpf
        customer.setCpf(customer.getCpf().replaceAll("[^0-9]", ""));

        // retirando os caracteres especiais do zipcode
        customer.getMainAddress().setZipCode(tratarZipCode(customer.getMainAddress().getZipCode()));

        // inserindo as datas
        customer.setCreatedAt(Calendar.getInstance().getTime());
        customer.setUpdateAt(Calendar.getInstance().getTime());

        HashMap<String, String> filters = new HashMap<String, String>();
        CustomerDAOImpl customerDAOImpl = injector.getInstance(CustomerDAOImpl.class);
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);
        Customer customerResult = customerDAOImpl.insert(customer, jdbi);
        filters.put("customer_id", Integer.toString(customerResult.getId()));
        customer.getMainAddress().setCustomer_id(customerResult.getId());
        Address addressResult = addressDAOImpl.insert(customer.getMainAddress(), jdbi);
        customerResult.setMainAddress(addressResult);
        List<Address> addresses = addressDAOImpl.select(filters, jdbi);
        customerResult.setAddresses(addresses);

        try {
            jsonString = mapper.writeValueAsString(customerResult);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonString = tratarJson(jsonString);

        return jsonString;
    }

    @Override
    public String updateCustomer(Customer customer, Injector injector, Jdbi jdbi) {

        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "";

        // retirando os caracteres especiais do cpf
        if (customer.getCpf() != null){
                customer.setCpf(customer.getCpf().replaceAll("[^0-9]", ""));
        }

        // inserindo as datas
        customer.setUpdateAt(Calendar.getInstance().getTime());

        CustomerDAOImpl customerDAOImpl = injector.getInstance(CustomerDAOImpl.class);
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);
        customerDAOImpl.update(customer, jdbi);

        if (customer.getMainAddress() != null) {
            // retirando os caracteres especiais do zipcode
            customer.getMainAddress().setZipCode(tratarZipCode(customer.getMainAddress().getZipCode()));
            customer.getMainAddress().setCustomer_id(customer.getId());
            addressDAOImpl.update(customer.getMainAddress(), jdbi);
        }
        jsonString = getCustomerById(String.valueOf(customer.getId()), injector, jdbi);

        return jsonString;
    }

    @Override
    public String listCustomers(String json, Injector injector, Jdbi jdbi) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        CustomerDAOImpl customerDAOImpl = injector.getInstance(CustomerDAOImpl.class);
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);

        try{
            HashMap<String, String> filters = mapper.readValue(json, HashMap.class);
            if (filters.containsKey("id")){
                String customedId = String.valueOf(filters.get("id"));
                filters.put("id", customedId);
            }
            List<Customer> customersList = customerDAOImpl.select(filters, jdbi);

            if (customersList.size() == 0) {
                throw new IllegalArgumentException(getJsonException("listCustomers"));
            }

            for (int i = 0; i < customersList.size(); i++){
                filters.clear();
                filters.put("customer_id", String.valueOf(customersList.get(i).getId()));
                filters.put("main", "1");

                List<Address> mainAddress = addressDAOImpl.select(filters, jdbi);
                customersList.get(i).setMainAddress(mainAddress.get(0));

                filters.remove("main");
                List<Address> addresses = addressDAOImpl.select(filters, jdbi);
                customersList.get(i).setAddresses(addresses);
            }


            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(customersList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonString = tratarJson(jsonString);

        return jsonString;
    }

    @Override
    public String getCustomerById(String id, Injector injector, Jdbi jdbi) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        CustomerDAOImpl customerDAOImpl = injector.getInstance(CustomerDAOImpl.class);
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);

        try{
            HashMap<String, String> filters = new HashMap<String, String>();
            String customedId = id;
            filters.put("id", customedId);
            List<Customer> customersList = customerDAOImpl.select(filters, jdbi);

            if (customersList.size() == 0) {
                throw new IllegalArgumentException(getJsonException("listCustomers"));
            }

            filters.clear();
            filters.put("customer_id", customedId);

            for (int i = 0; i < customersList.size(); i++){
                filters.put("main", "1");
                List<Address> mainAddress = addressDAOImpl.select(filters, jdbi);
                customersList.get(i).setMainAddress(mainAddress.get(0));
                filters.remove("main");
                List<Address> addresses = addressDAOImpl.select(filters, jdbi);
                customersList.get(i).setAddresses(addresses);
            }

            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(customersList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonString = tratarJson(jsonString);

        return jsonString;
    }

    private String tratarJson(String json){
        String jsonString = json;

        jsonString = jsonString.replace("addresses", "endereco");
        jsonString = jsonString.replace("address", "mainAddress");
        jsonString = jsonString.replace("endereco", "addresses");

        return jsonString;
    }

    private String tratarZipCode(String zipCode){
        return zipCode.replaceAll("[^0-9]", "");
    }

    @Override
    public String createAddress(Address address, Injector injector, Jdbi jdbi) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);
        address.setZipCode(tratarZipCode(address.getZipCode()));

        HashMap<String, String> filters = new HashMap<String, String>();
        if (address.isMain()){
            filters.put("main", "1");
            List<Address> mainAddress = addressDAOImpl.select(filters, jdbi);

            if (mainAddress != null){
                throw new IllegalArgumentException(getJsonException("checkMainAddresss"));
            }
        }
        Address addressResult = addressDAOImpl.insert(address, jdbi);

        try {
            jsonString = mapper.writeValueAsString(addressResult);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonString = tratarJson(jsonString);

        return jsonString;
    }

    @Override
    public String listAddresses(HashMap<String, String> filters, Injector injector, Jdbi jdbi) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);

        try{
            List<Address> addressesList = addressDAOImpl.select(filters, jdbi);
            if (addressesList.size() == 0) {
                throw new IllegalArgumentException(getJsonException("listAddresses"));
            }
            jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(addressesList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonString = tratarJson(jsonString);

        return jsonString;
    }

    @Override
    public String updateAddresss(HashMap<String, String> filters, Address address, Injector injector, Jdbi jdbi) {

        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "";

        if (address.getZipCode() != null) {
            address.setZipCode(tratarZipCode(address.getZipCode()));
        }

        /* TODO INSERIR VALIDAÇÃO PARA CPF*/
        AddressDAOImpl addressDAOImpl = injector.getInstance(AddressDAOImpl.class);
        address.setId(Integer.parseInt(filters.get("id")));
        address.setCustomer_id(Integer.parseInt(filters.get("customer_id")));
        addressDAOImpl.update(address, jdbi);

        try{
            List<Address> addressesList = addressDAOImpl.select(filters, jdbi);
            jsonString = mapper.writeValueAsString(addressesList.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonString = tratarJson(jsonString);

        return jsonString;
    }

    private int getIdade(Date nascimento) {
        Calendar cn = Calendar.getInstance();
        cn.setTime(nascimento);

        Date dataAtual = new Date(System.currentTimeMillis());
        Calendar ca = Calendar.getInstance();
        ca.setTime(dataAtual);

        int idade = ca.get(Calendar.YEAR) - cn.get(Calendar.YEAR);
        if (ca.get(Calendar.MONTH) < cn.get(Calendar.MONTH)) {
            idade--;
        } else if (ca.get(Calendar.MONTH) == cn.get(Calendar.MONTH)) {
            if (ca.get(Calendar.DAY_OF_MONTH) < cn.get(Calendar.DAY_OF_MONTH))
                idade--;
        }
        return idade;
    }

    private static Date formataData(String data) throws Exception {
        if (data == null || data.equals(""))
            return null;

        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = new java.sql.Date( ((Date)formatter.parse(data)).getTime() );
        } catch (ParseException e) {
            throw e;
        }
        return date;
    }

    @Override
    public String getJsonException(String code){

        if (code == "createCustomer") {
            return "{\n" +
                    "  \"code\": \"createCostumer\",\n" +
                    "  \"description\": \"Nome do cliente é um campo obrigatório.\"\n" +
                    "}";
        } else if (code == "ageCheck"){
            return "{\n" +
                    "  \"code\": \"ageCheck\",\n" +
                    "  \"description\": \"Customer não pode ter mais que 100 anos.\"\n" +
                    "}";
        } else if ((code == "listCustomers") || (code == "updateCustomer") || (code == "deleteCustomer")){
            return "{\n" +
                    "  \"code\": \"listar_cliente\",\n" +
                    "  \"description\": \"Customer não encontrado\"\n" +
                    "}";
        }  else if (code == "listAddresses"){
            return "{\n" +
                    "  \"code\": \"listar_cliente\",\n" +
                    "  \"description\": \"Não foi encontrado nenhum endereço\"\n" +
                    "}";
        } else if ((code == "listAddresses") || (code == "updateAddresss") || (code == "deleteAddress")){
            return "{\n" +
                    "  \"code\": \"listar_cliente\",\n" +
                    "  \"description\": \"Não foi encontrado nenhum endereço\"\n" +
                    "}";
        } else if ((code == "checkMainAddresss")){
            return "{\n" +
                    "  \"code\": \"checkMainAddress\",\n" +
                    "  \"description\": \"Já existe um endereço principal cadastrado para esse customer\"\n" +
                    "}";
        } else {
            return "{\n" +
                    "  \"code\": \"listar_cliente\",\n" +
                    "  \"description\": \"Erro Interno do Servidor\"\n" +
                    "}";
        }
    }
}
