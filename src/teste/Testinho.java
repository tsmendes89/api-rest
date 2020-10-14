import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Testinho implements TesteService {

    @Override
    public String returnJson() {

        Staff staff = createStaff();
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "";

        try {
            jsonString = mapper.writeValueAsString(staff);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalJsonString = jsonString;

        return finalJsonString;
    }

    private static Staff createStaff(){

        Staff staff = new Staff();

        staff.setName("Thiago");
        staff.setAge(35);

        return staff;
    }
}
