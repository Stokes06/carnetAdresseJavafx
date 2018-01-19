package com.humanbooster.javafx.controller;

import java.io.File;

import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.humanbooster.javafx.model.Person;
import com.humanbooster.javafx.model.PersonListWrapper;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<Person> personData = FXCollections.observableArrayList();

    public MainApp(){
        personData.add(new Person("Kelthoum", "Imoussaïne"));
        personData.add(new Person("Safia", "Gharbi"));
        personData.add(new Person("Rachida", "Elmoussati"));
        personData.add(new Person("Ibrahim", "Gharbi"));
        personData.add(new Person("Bilel", "Gharbi"));
        personData.add(new Person("Aïcha", "Gharbi"));
        personData.add(new Person("Odile", "Marc"));
        personData.add(new Person("Asma", "Imoussaïne"));
        personData.add(new Person("Hanna", "Kharboutli"));
        personData.add(new Person("Elisabeth", "Pichon"));
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Carnet d'addresses");

        this.primaryStage.getIcons().add(new Image("file:resources/images/smiley.jpeg"));

        initRootLayout();

        showPersonOverview();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(
            		"/com/humanbooster/javafx/view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = getPersonFilePath();
        if (file != null) {
            loadPersonDataFromFile(file);
        }
    }

    public void showPersonOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(
            		"/com/humanbooster/javafx/view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(personOverview);

            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showPersonEditDialog(Person person) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(
            			"/com/humanbooster/javafx/view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edition d'une Personne");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

   public File getPersonFilePath() {
       Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
       String filePath = prefs.get("file:resources/persons.xml", null);
       if (filePath != null) {
           return new File(filePath);
       } else {
           return null;
       }
   }

   public void setPersonFilePath(File file) {
       Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
       if (file != null) {
           prefs.put("file:resources/persons.xml", file.getPath());

           primaryStage.setTitle("AddressApp - " + file.getName());
       } else {
           prefs.remove("file:resources/persons.xml");

           primaryStage.setTitle("Carnet d'adresses");
       }
   }

   public void loadPersonDataFromFile(File file) {
       try {
           JAXBContext context = JAXBContext
                   .newInstance(PersonListWrapper.class);
           Unmarshaller um = context.createUnmarshaller();

           PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

           personData.clear();
           personData.addAll(wrapper.getPersons());

           setPersonFilePath(file);

       } catch (Exception e) {
           Alert alert = new Alert(AlertType.ERROR);
           alert.setTitle("Erreur");
           alert.setHeaderText("Chargement des données impossible");
           alert.setContentText("Echec du chargement des données "
           		+ "depuis le fichier \n" + file.getPath());

           alert.showAndWait();
       }
   }

   public void savePersonDataToFile(File file) {
       try {
           JAXBContext context = JAXBContext
                   .newInstance(PersonListWrapper.class);
           Marshaller m = context.createMarshaller();
           m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

           PersonListWrapper wrapper = new PersonListWrapper();
           wrapper.setPersons(personData);

           m.marshal(wrapper, file);

           setPersonFilePath(file);
       } catch (Exception e) {
           Alert alert = new Alert(AlertType.ERROR);
           alert.setTitle("Erreur");
           alert.setHeaderText("Echec de l'enregistrement des données");
           alert.setContentText("Echec de l'enregistrement des données "
           		+ "dans le fichier \n" + file.getPath());
           alert.showAndWait();
       }
   }

   public void showBirthdayStatistics() {
       try {
           FXMLLoader loader = new FXMLLoader();
           loader.setLocation(MainApp.class.getResource(
        		   			"/com/humanbooster/javafx/view/BirthdayStatistics.fxml"));
           AnchorPane page = (AnchorPane) loader.load();
           Stage dialogStage = new Stage();
           dialogStage.setTitle("Statistiques des anniversaires");
           dialogStage.initModality(Modality.WINDOW_MODAL);
           dialogStage.initOwner(primaryStage);
           Scene scene = new Scene(page);
           dialogStage.setScene(scene);

           BirthdayStatisticsController controller = loader.getController();
           controller.setPersonData(personData);

           dialogStage.show();

       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   public Stage getPrimaryStage() {
       return primaryStage;
   }

   public ObservableList<Person> getPersonData() {
	   return personData;
   }

   public void setPersonData(ObservableList<Person> personData) {
	   this.personData = personData;
   }

   public static void main(String[] args) {
	   launch(args);
   }
}