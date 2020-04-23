import java.io.IOException;
import java.sql.SQLException;

public class Surgeon extends Doctor {

    private String name;
    private int score;


    public String getName() {
        return name;
    }

    @Override
    public  void doTreatment() throws IOException, IllegalAccessException, NoSuchFieldException, InstantiationException, SQLException {
        System.out.println();
        System.out.println(getName() + ": Прошу описать вашу жалобу точнее.");
        try {
        Patient.speakingWithDoctors();}
        catch (IOException e) {
        e.printStackTrace();
    }
        System.out.println(getName() + ": Теперь Ваш опорно-двигательный аппарат в полном порядке!");

        score = DbService.getDoctorsScoreByDoctorsName(this.getName());

        Registry.requestOfPatientOpinions(this, score, this.getName());

    }
}
