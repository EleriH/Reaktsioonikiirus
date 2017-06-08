import javafx.application.Application;
import javafx.stage.Stage;

public class Reaktsioonikiirus extends Application {

    public Reaktsioonikiirus() {

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Mäng mäng = new Mäng();

        primaryStage.setTitle("Reaktsioonikiirus");
        primaryStage.setScene(mäng.getStseen());
        primaryStage.sizeToScene();
        primaryStage.show();
    }

}
