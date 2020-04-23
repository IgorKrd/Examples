import java.io.IOException;
import java.sql.SQLException;

public class ENT extends Doctor {
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
        System.out.println(getName() + ": Ваш слух и нормальное дыхание носом восстановлено полностью.");

        score = DbService.getDoctorsScoreByDoctorsName(this.getName());

        Registry.requestOfPatientOpinions(this, score, this.getName());
    }
}
