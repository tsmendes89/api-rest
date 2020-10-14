import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

import static spark.Spark.get;

public class TEste {

    public static void main(String[] args) {


        Injector injector = Guice.createInjector(new TesteInjector());
        Testinho testinho = injector.getInstance(Testinho.class);

        String finalJsonString = testinho.returnJson();

        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/teste", "root", "");

        List<String> names = jdbi.withHandle(handle -> handle.createQuery("select nome from tabela")
                .mapTo(String.class)
                .list());

        get("/hello", (req, res) -> names.get(0));
    }
}

