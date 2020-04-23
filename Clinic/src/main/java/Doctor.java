import java.io.IOException;
import java.sql.SQLException;

public abstract class Doctor {

    public void hippocratesOath() {
        System.out.println("Клятва Гиппократа");
    }

    public abstract void doTreatment() throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException, SQLException;
}
