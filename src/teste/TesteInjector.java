import com.google.inject.AbstractModule;

public class TesteInjector extends AbstractModule{

    @Override
    protected void configure() {
        bind(TesteService.class).to(Testinho.class);
    }
}
