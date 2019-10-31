import java.sql.*;


public class DbService {

    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    private static final String url = "jdbc:mysql://localhost:3306/ClinicDB";
    private static final String user = "root";
    private static final String password = "Exist2019K@#";

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        stmt = connection.createStatement();
    }

    public static int getRoomNumberByCause(String doctorType) throws SQLException {

        String sql = String.format("select roomNumber FROM doctors where doctorType = '%s' ", doctorType);
        rs = stmt.executeQuery(sql);
        rs.next();

        return rs.getInt(1);
    }

    public static String getDoctorsTypeByRoomNumber(int roomNumber) throws SQLException {
        if (roomNumber > 0) {
            String sql = String.format("select doctorType FROM doctors where roomNumber = '%s' ", roomNumber);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString(1);
        } else return null;

    }

    public static String getDoctorsNameByRoomNumberAndDoctorType(int roomNumber, String doctorType) throws SQLException {
        if (roomNumber > 0 && doctorType != null) {
            String sql = String.format("select doctorName FROM doctors where roomNumber = '%s' and doctorType = '%s' ", roomNumber, doctorType);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();

            return rs.getString(1);
        }else return null;
    }


    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
