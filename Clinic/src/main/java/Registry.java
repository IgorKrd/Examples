
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Registry {

    private static int roomNumber;
    private static String doctorsType;
    private static String doctorsName;


    public static void askToPatient() throws IOException, SQLException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        System.out.println("Добрый день! Как Вас зовут?");

        Patient.getPatientInfo();

        while (true) {
            System.out.println("На что жалуетесь? Уважаемый " + Patient.getName() + "!");
            Patient.requestToTreatment();
            String req = Patient.getRequest();

            if (!req.equals("")) {
                getDoctorsRoomNumber(req);
                getDoctorsType(roomNumber);
                referralPatientToDoctor();
                break;
            } else {
                System.out.println("Прошу Вас сообщить причину обращения к нам!");
            }
        }
    }


    public static int getDoctorsRoomNumber(String request) throws SQLException {

        if (request.contains("зуб") || request.contains("зубы") || request.contains("десна") || request.contains("дёсны")) {
            roomNumber = DbService.getRoomNumberByCause("Dentist");

        } else if (request.contains("глаз") || request.contains("глаза")) {
            roomNumber = DbService.getRoomNumberByCause("Ophthalmologist");

        } else if (request.contains("ухо") || request.contains("нос") || request.contains("уши") || request.contains("горло")) {
            roomNumber = DbService.getRoomNumberByCause("ENT");

        } else if (request.contains("нога") || request.contains("рука") || request.contains("спина") || request.contains("шея") || request.contains("поясница")) {
            roomNumber = DbService.getRoomNumberByCause("Surgeon");

        } else return 0;

        return roomNumber;
    }

    public static String getDoctorsType(int roomNumber) throws SQLException {
        if (roomNumber > 0) {
            doctorsType = DbService.getDoctorsTypeByRoomNumber(roomNumber);
            return doctorsType;
        } else return null;
    }

    public static String getDoctorsName(int roomNumber, String doctorsType) throws SQLException {
        if (roomNumber > 0 && doctorsType != null) {
            doctorsName = DbService.getDoctorsNameByRoomNumberAndDoctorType(roomNumber, doctorsType);
            return doctorsName;
        } else return null;
    }

    public static void referralPatientToDoctor() throws SQLException, IOException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        roomNumber = getDoctorsRoomNumber(Patient.getRequest());
        doctorsType = getDoctorsType(roomNumber);
        doctorsName = getDoctorsName(roomNumber, doctorsType);

        if (roomNumber > 0 && doctorsType != null && doctorsName != null) {

            ///// Применение порождающего паттерна "Builder" для формирования текста направления пациента к конкретному врачу.

            StringBuilder builder = new StringBuilder();
            builder.append("Уважаемый ");
            builder.append(Patient.getName());
            builder.append(", Вам необходимо пройти в кабинет № ");
            builder.append(roomNumber);
            builder.append(" на приём к врачу ");
            builder.append(doctorsType);
            builder.append(" ");
            builder.append(doctorsName);
            System.out.println(builder.toString());

//            System.out.println("Уважаемый " + Patient.getName() + ", Вам необходимо пройти в кабинет № " + roomNumber +
//                    " на приём к врачу " + doctorsName);

        } else {
            System.out.println("К сожалению у нас нет такого специалиста.");
            return;
        }
        Patient.goToDoctor(roomNumber, doctorsType, doctorsName);
    }

    public static void requestOfPatientOpinions(Doctor doctor, int score, String doctorsName) throws IOException, NoSuchFieldException, IllegalAccessException, InstantiationException, SQLException {
        System.out.println("Уважаенмый " + Patient.getName() + "! Прошу дать Вашу оценку действий врача: " + doctor.getClass().getName() + " " + doctorsName);
        Patient.opinionsOfTreatment();

        if (Patient.getOpinions().contains("отлично") || Patient.getOpinions().contains("замечательно")) {
            DbService.changeDoctorsScoreByDoctorsName(doctorsName, calculateDoctorsScore(doctor, 15));
            System.out.println("Оценка - 'Отлично'");

            System.out.println("Спасибо за Ваш отзыв о нашей работе! Ваша высокая оценка помогает нам улучшать качество лечения!");
        }
        if (Patient.getOpinions().contains("хорошо") || Patient.getOpinions().contains("удовлетворён")) {
            DbService.changeDoctorsScoreByDoctorsName(doctorsName, calculateDoctorsScore(doctor, 10));
            System.out.println("Оценка - Хорошо");

            System.out.println("Спасибо за Ваш отзыв о нашей работе! Мы работаем над улучшением качества лечения!");

        } else if (Patient.getOpinions().contains("плохо") || Patient.getOpinions().contains("не удовлетворён")) {
            DbService.changeDoctorsScoreByDoctorsName(doctorsName, calculateDoctorsScore(doctor, 5));
            System.out.println("Оценка -  Плохо");

            System.out.println("Спасибо за Ваш отзыв о нашей работе! Мы примем меры по улучшения качества лечения!");
        }
    }

    public static int calculateDoctorsScore(Doctor doctor, int increment) throws NoSuchFieldException, IllegalAccessException {
        Field fieldScore = doctor.getClass().getDeclaredField("score");
        fieldScore.setAccessible(true);
        int currentDoctorScore = (int) fieldScore.get(doctor);
        System.out.println("Текущая оценка врача: " + currentDoctorScore); // убрать эту строку
        fieldScore.set(doctor, currentDoctorScore + increment);

        return (int) fieldScore.get(doctor);
    }


}
