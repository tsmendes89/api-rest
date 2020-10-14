package br.com.composablebit.application.service.database;

import org.jdbi.v3.core.Jdbi;

public interface Connection {

     Jdbi connectToDatabase();
}
