import java.sql.*;


public class DbService {

    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    private static final String url = "jdbc:mysql://localhost:3306/ClinicDb";
    private static final String user = "root";
    private static final String password = "Exist2019K@#";

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        stmt = connection.createStatement();
    }

    public static int getRoomNumberByCause(String doctorsType) throws SQLException {

        String sql = String.format("select roomNumber FROM doctors where doctorsType = '%s' ", doctorsType);
        rs = stmt.executeQuery(sql);
        rs.next();

        return rs.getInt(1);
    }

    public static String getDoctorsTypeByRoomNumber(int roomNumber) throws SQLException {
        if (roomNumber > 0) {
            String sql = String.format("select doctorsType FROM doctors where roomNumber = '%s' ", roomNumber);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getString(1);
        } else return null;
    }

    public static String getDoctorsNameByRoomNumberAndDoctorType(int roomNumber, String doctorsType) throws SQLException {
        if (roomNumber > 0 && doctorsType != null) {
            String sql = String.format("select doctorsName FROM doctors where roomNumber = '%s' and doctorsType = '%s' ", roomNumber, doctorsType);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();

            return rs.getString(1);
        }else return null;
    }

    public static int getDoctorsScoreByDoctorsName(String doctorsName) throws SQLException {
        if (doctorsName != null) {
            String sql = String.format("select doctorsScore FROM doctors where doctorsName = '%s' ", doctorsName);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();

            return rs.getInt(1);
        }else return 0;
    }

    public static void changeDoctorsScoreByDoctorsName(String doctorsName, int newScore) throws SQLException {
        if (doctorsName != null && newScore != 0) {

            System.out.println("Обновлённая оценка врача: " + newScore + " : " + doctorsName);

            String sql = String.format("update doctors SET doctorsScore = '%s' where doctorsName = '%s' ", newScore, doctorsName);
            stmt.executeUpdate(sql);
        }
    }

        public static void disconnect () {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
