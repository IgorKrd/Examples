

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Main {

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public String request;


    {
        try {
            request = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            DbService.connect();
            Registry.askToPatient();

            DbService.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
