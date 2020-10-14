package br.com.composablebit.application.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private Properties properties;

    public PropertiesReader(String nomeArquivo){
        properties = new Properties();

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(nomeArquivo);

        try{
            properties.load(inputStream);
            inputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getValor(String chave){
        return properties.getProperty(chave);
    }
}
