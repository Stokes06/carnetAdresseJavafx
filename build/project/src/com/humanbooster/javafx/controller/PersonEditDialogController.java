package com.humanbooster.javafx.controller;

import com.humanbooster.javafx.model.Person;
import com.humanbooster.javafx.utils.DateUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PersonEditDialogController {

	@FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField birthdayField;

    private Stage dialogStage;

    private Person person;

    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPerson(Person person) {
        this.person = person;

        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());
        streetField.setText(person.getStreet());
        postalCodeField.setText(Integer.toString(person.getPostalCode()));
        cityField.setText(person.getCity());
        birthdayField.setText(DateUtil.format(person.getBirthday()));
        birthdayField.setPromptText("dd.mm.yyyy");
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            person.setFirstName(firstNameField.getText());
            person.setLastName(lastNameField.getText());
            person.setStreet(streetField.getText());
            person.setPostalCode(Integer.parseInt(postalCodeField.getText()));
            person.setCity(cityField.getText());
            person.setBirthday(DateUtil.parse(birthdayField.getText()));

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "Le prénom est mal renseigné\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "Le nom est mal renseigné\n";
        }
        if (streetField.getText() == null || streetField.getText().length() == 0) {
            errorMessage += "La rue est mal renseigné\n";
        }

        if (postalCodeField.getText() == null || postalCodeField.getText().length() == 0) {
            errorMessage += "Le code postal est mal renseigné\n";
        } else {
            try {
                Integer.parseInt(postalCodeField.getText());
            } catch (NumberFormatException nfe) {
                errorMessage += "Le code postal est un nombre\n";
            }
        }

        if (cityField.getText() == null || cityField.getText().length() == 0) {
            errorMessage += "La ville est mal renseignée\n";
        }

        if (birthdayField.getText() == null || birthdayField.getText().length() == 0) {
            errorMessage += "La date de naissance est mal renseignée\n";
        } else {
            if (!DateUtil.validDate(birthdayField.getText())) {
                errorMessage += "La date de naissance a pour format dd.mm.yyyy!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Les champs sont mal renseignés");
            alert.setHeaderText("Veuillez corriger les champs concernés");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
