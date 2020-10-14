package br.com.composablebit.application.service.database.impl;

import br.com.composablebit.application.service.PropertiesReader;
import br.com.composablebit.application.service.database.Connection;
import org.jdbi.v3.core.Jdbi;

public class ConnectionImpl implements Connection {
    @Override
    public Jdbi connectToDatabase() {

        PropertiesReader propertiesReader = new PropertiesReader("conexao.properties");

        System.out.println("Url " + propertiesReader.getValor("url"));

        return Jdbi.create(propertiesReader.getValor("url"), propertiesReader.getValor("username"), propertiesReader.getValor("password"));
    }
}
