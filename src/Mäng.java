import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mäng {

    private static String[] värviNimed = {"Punane", "Oranž", "Kollane", "Roheline", "Sinine", "Lilla"};
    private static String[] värviStiilid = {"punane", "oranz", "kollane", "roheline", "sinine", "lilla"};
    private static int nuppudeArv = 3;
    private static int ringideArv = 10;
    private static double veaTrahv = 1.0;

    private final Random random = new Random();

    private final Scene stseen;
    private final List<Button> nupud;
    private final Label aegLabel;
    private final Label ringLabel;
    private final Label viguLabel;
    private final Label tulemusLabel;
    private final Timeline ringiTimeline;
    private final VBox mänguPane;
    private final VBox algusPane;
    private final Button startNupp;

    private int ring;
    private long mänguAlgusAeg;
    private long ringiAlgusAeg;
    private int õigeNupp;
    private int vigu;


    public Mäng() {
        nupud = new ArrayList<>();

        HBox nupuBox = new HBox(10);
        nupuBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < nuppudeArv; i++) {
            Button nupp = new Button("Nupp");
            nupp.setPrefSize(150, 150);
            nupp.getStyleClass().add("nupp");

            int finalI = i;
            nupp.setOnAction(ae -> {
                nupuvajutus(finalI);
            });
            nupud.add(nupp);

            nupuBox.getChildren().add(nupp);
        }

        aegLabel = new Label();
        ringLabel = new Label();
        viguLabel = new Label();

        Label aegKiri = new Label();
        aegKiri.setText("Aeg:");
        aegKiri.getStyleClass().add("tiitel");

        Label ringKiri = new Label();
        ringKiri.setText("Ring:");
        ringKiri.getStyleClass().add("tiitel");

        Label viguKiri = new Label();
        viguKiri.setText("Vigu:");
        viguKiri.getStyleClass().add("tiitel");


        GridPane olek = new GridPane();
        olek.getStyleClass().add("olek");
        olek.setHgap(10);
        olek.addRow(1, ringKiri, aegKiri, viguKiri);
        olek.addRow(2, ringLabel, aegLabel, viguLabel);
        GridPane.setHgrow(ringKiri, Priority.ALWAYS);
        GridPane.setHgrow(ringLabel, Priority.ALWAYS);
        GridPane.setHgrow(aegKiri, Priority.ALWAYS);
        GridPane.setHgrow(aegLabel, Priority.ALWAYS);
        GridPane.setHgrow(viguKiri, Priority.ALWAYS);
        GridPane.setHgrow(viguLabel, Priority.ALWAYS);

        mänguPane = new VBox(10);
        mänguPane.setPadding(new Insets(10, 10, 10, 10));
        mänguPane.getChildren().addAll(olek, nupuBox);
        mänguPane.setAlignment(Pos.CENTER);
        mänguPane.setVisible(false);
        mänguPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            try {
                nupuvajutus(Integer.valueOf(event.getText()) - 1);
            } catch (NumberFormatException ignored) {
                // Vale nupp
            }
        });

        algusPane = new VBox(30);
        algusPane.setFillWidth(true);
        algusPane.setAlignment(Pos.CENTER);

        startNupp = new Button("Alusta");
        startNupp.setPrefSize(120, 120);
        startNupp.getStyleClass().addAll("nupp", "hall");
        startNupp.setOnAction(ae -> {
            alustaMäng();
        });

        tulemusLabel = new Label();
        tulemusLabel.getStyleClass().add("tulemus");

        algusPane.getChildren().addAll(startNupp, tulemusLabel);

        StackPane root = new StackPane();
        root.getChildren().addAll(mänguPane, algusPane);

        stseen = new Scene(root, 900, 600);
        stseen.getStylesheets().add("stiil.css");

        ringiTimeline = new Timeline(new KeyFrame(
                Duration.millis(9),
                ae -> tiks()));
        ringiTimeline.setCycleCount(Animation.INDEFINITE);

        startNupp.requestFocus();
    }

    public Scene getStseen() {
        return stseen;
    }

    /**
     * Seab antud indeksiga nupule etteantud teksti ja värvi
     * @param nupuIndex
     * @param tekst
     * @param värv
     */
    private void seaNupuVärvid(int nupuIndex, int tekst, int värv) {
        Button nupp = nupud.get(nupuIndex);

        nupp.getStyleClass().removeAll(värviStiilid);
        nupp.getStyleClass().add(värviStiilid[värv]);
        nupp.setText(värviNimed[tekst]);
    }

    /**
     * Seab kõigile nuppudele juhusliku teksti ja värvi. Ühe nupu värvus ja tekst vastavad, kõigil teistel mitte.
     * Õige nupu indeks kirjutatakse isendivälja õigeNupp
     */
    private void juhuslikustaNupud() {
        õigeNupp = random.nextInt(nuppudeArv);
        final int värvideArv = värviNimed.length;

        for (int i = 0; i < nuppudeArv; i++) {
            if (i == õigeNupp) {
                int värv = random.nextInt(värvideArv);
                seaNupuVärvid(i, värv, värv);
            } else {
                int värv = random.nextInt(värvideArv);
                int tekst = (värv + 1 + random.nextInt(värvideArv - 1)) % värvideArv;
                seaNupuVärvid(i, tekst, värv);
            }
        }
    }

    /**
     * Alustab mängu. St. näitab mängupaneeli, seab vajalikud isendiväljad (ring, vigu, mänguAlgusAeg) algasendisse ja
     * käivitab esimese ringi.
     */
    private void alustaMäng() {
        mänguPane.setVisible(true);
        algusPane.setVisible(false);
        mänguPane.requestFocus();
        ring = 0;
        vigu = 0;
        alustaRing();
    }

    /**
     * Alustab ringi. Märgib üles ringi algusaja ja käivitab taimeri näitamise.
     * <p>
     * Juhul kui on tegemist ringi indeksiga 0, seab mängu algusaja samaks ringi algusajaga.
     * <p>
     * Juhul kui ringi indeks == ringide arv mängus, lõpetab mängu.
     **/
    private void alustaRing() {
        if (ring == ringideArv) {
            lõpetaMäng();

        } else {
            viguLabel.setText(Integer.toString(vigu));
            ringLabel.setText(Integer.toString(ring + 1));
            juhuslikustaNupud();
            ringiAlgusAeg = System.currentTimeMillis();
            if (ring == 0) {
                mänguAlgusAeg = ringiAlgusAeg;
            }
            tiks();
            ringiTimeline.playFromStart();
        }
    }

    /**
     * Reageerib kasutaja nupuvajutusele. Õige nupu puhul alustab järgmist ringi. Vale nupu puhul käivitab nupu
     * väristamise animatsiooni.
     * @param i Vajutatud nupu indeks
     */
    private void nupuvajutus(int i) {
        if ((i < 0) || (i >= nupud.size())) {
            return;
        }
        if (i == õigeNupp) {
            ringiTimeline.stop();
            ring++;
            alustaRing();
        } else {
            vigu++;
            viguLabel.setText(Integer.toString(vigu));

            TranslateTransition tt = new TranslateTransition(Duration.millis(50), nupud.get(i));
            tt.setFromY(0f);
            tt.setByY(10f);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);

            tt.playFromStart();
        }
    }

    /**
     * Värskendab kellaaega taimeril
     */
    private void tiks() {
        long ringiaeg = System.currentTimeMillis() - ringiAlgusAeg;
        long mänguaeg = ringiAlgusAeg - mänguAlgusAeg;
        String tekst = String.format("%.3f + %.3f s", mänguaeg * 0.001, ringiaeg * 0.001);
        aegLabel.setText(tekst);
    }

    /**
     * Lõpetab mängu. Peidab mängu paneeli ja toob nähtavale mängu alguspaneeli. Näitab lõpetatud mängu skoori.
     */
    private void lõpetaMäng() {
        long lõppAeg = System.currentTimeMillis();
        mänguPane.setVisible(false);
        algusPane.setVisible(true);
        startNupp.requestFocus();

        double aeg = (lõppAeg - mänguAlgusAeg) * 0.001;

        tulemusLabel.setText(String.format("Skoor %.3f (%.3f sekundit ja %d viga)",
                aeg + vigu * veaTrahv,
                aeg,
                vigu
        ));
    }

}

