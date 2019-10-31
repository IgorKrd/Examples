import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;


public class Patient {

    public static String request;
    private static String name;


    public static void setName(String patientName) {
        Patient.name = patientName;
    }


    public static String getName() {
        return name;
    }


    public static void  speak() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s =  reader.readLine();
        Patient patient = new Patient();
        patient.setName(s);

    }

    public static String requestToTreatment() throws IOException, SQLException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        request = reader.readLine();

        return request;
    }

    public static void goToDoctor(int roomNumber, String doctorType, String doctorName) throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        System.out.println();
        System.out.println("Пациент пошёл на приём к врачу " + doctorName + " в кабинет № " + roomNumber);


        Class<?> doctor = Class.forName(doctorType);
        Object doc = doctor.newInstance();
        Class.forName(doctorType).getMethod("doTreatment").invoke(doc);





        }


    }

