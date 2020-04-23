import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Patient {

    private static String request;
    private static String complaint;
    private static String opinions;
    private static String name;

    public static String getRequest() {
        return request;
    }

    public static String getOpinions() {
        return opinions;
    }


    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Patient.name = name;
    }

    public static void getPatientInfo() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = reader.readLine();
        Patient patient = new Patient();
        patient.setName(s);

    }

    public static String requestToTreatment() throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        request = reader.readLine();
        return request;

    }

    public static void goToDoctor(int roomNumber, String doctorsType, String doctorsName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        System.out.println();
        System.out.println("Пациент пошёл на приём к врачу " + doctorsType + " " + doctorsName + " в кабинет № " + roomNumber);

        Class<?> doctor = Class.forName(doctorsType);
        Object doc = doctor.newInstance();
        Field fieldName = Class.forName(doctorsType).getDeclaredField("name");
        fieldName.setAccessible(true);
        fieldName.set(doc, doctorsName); // В классе соотвествующего доктора назначаем значение полю name
        Class.forName(doctorsType).getMethod("doTreatment").invoke(doc);

    }

    public static String speakingWithDoctors() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        complaint = reader.readLine();
        return complaint;
    }

    public static String opinionsOfTreatment() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        opinions = reader.readLine();
        return opinions;
    }


}

