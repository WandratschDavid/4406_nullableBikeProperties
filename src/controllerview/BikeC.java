package controllerview;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;
import model.Bike;
import model.Farbe;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * Controller-Klasse Bike für View bikeV.fxml
 */
public class BikeC
{
	private static final NumberFormat NUMBERFORMAT_2DECIMALS;

	static
	{
		NUMBERFORMAT_2DECIMALS = NumberFormat.getNumberInstance();
		NUMBERFORMAT_2DECIMALS.setMaximumFractionDigits(2);
		NUMBERFORMAT_2DECIMALS.setMinimumFractionDigits(2);
	}

	@FXML
	private TextField tfRahmennr;
	@FXML
	private TextField tfMarkeType;
	@FXML
	private DatePicker dpVerfuegbar;
	@FXML
	private TextField tfText;
	@FXML
	private ChoiceBox<Farbe> cbFarbe;
	@FXML
	private TextField tfPreis;
	@FXML
	private Button btCancel;
	@FXML
	private Button btSelect;
	@FXML
	private Button btSave;

	private Bike model;
	private BooleanProperty hideNonkeyProperty = new SimpleBooleanProperty();

	/**
	 * View in gegebener Stage anzeigen
	 * @param stage gegebene Stage
	 */
	public static void show(Stage stage)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(BikeC.class.getResource("bikeV.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Bikes");
			stage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Platform.exit();
		}
	}

	/**
	 * Betätigung des Cancel-Buttons
	 * @param event
	 */
	@FXML
	void btCancelOnAction(ActionEvent event)
	{
		cancel();
	}

	/**
	 * Betätigung des Save-Buttons
	 * @param event
	 */
	@FXML
	void btSaveOnAction(ActionEvent event)
	{
		save();
	}

	/**
	 * Betätigung des Select-Buttons
	 * @param event
	 */
	@FXML
	void btSelectOnAction(ActionEvent event)
	{
		select();
	}

	/**
	 * Initialisierung. Wird vom FXML-Loader aufgerufen und dient zum Initialisieren der Controls, dem Einrichten von
	 * Bindings, etc.
	 */
	@FXML
	public void initialize()
	{
		// Controls initialisieren
		// Choicebox
		cbFarbe.setItems(Farbe.valuesAsObservableList());
		cbFarbe.setValue(cbFarbe.getItems().get(0));

		// Visibility
		tfRahmennr.disableProperty().bind(hideNonkeyProperty.not());
		tfMarkeType.disableProperty().bind(hideNonkeyProperty);
		dpVerfuegbar.disableProperty().bind(hideNonkeyProperty);
		tfText.disableProperty().bind(hideNonkeyProperty);
		cbFarbe.disableProperty().bind(hideNonkeyProperty);
		tfPreis.disableProperty().bind(hideNonkeyProperty);

		// Dis/Enable Buttons
		btSelect.disableProperty().bind(hideNonkeyProperty.not());
		btSave.disableProperty().bind(hideNonkeyProperty);

		// Start hidden
		hideNonkeyProperty.set(true);
	}

	/**
	 * Cancel-Button.
	 */
	private void cancel()
	{
		if (model != null)
		{
			tfRahmennr.textProperty().unbindBidirectional(model.rahmennrProperty());
			tfMarkeType.textProperty().unbindBidirectional(model.markeTypeProperty());
			dpVerfuegbar.valueProperty().unbindBidirectional(model.verfuegbarProperty());
			tfText.textProperty().unbindBidirectional(model.textProperty());
			cbFarbe.valueProperty().unbindBidirectional(model.farbeProperty());
			tfPreis.textProperty().unbindBidirectional(model.preisProperty());
		}

		tfRahmennr.setText("");
		tfMarkeType.setText("");
		dpVerfuegbar.setValue(null);
		tfText.setText("");
		cbFarbe.setValue(null);
		tfPreis.setText("");

		hideNonkeyProperty.set(true);
	}

	/**
	 * Select-Button.
	 */
	private void select()
	{
		try
		{
			model = Bike.select(tfRahmennr.getText());

			tfRahmennr.textProperty().bindBidirectional(model.rahmennrProperty());
			tfMarkeType.textProperty().bindBidirectional(model.markeTypeProperty());
			dpVerfuegbar.valueProperty().bindBidirectional(model.verfuegbarProperty());
			tfText.textProperty().bindBidirectional(model.textProperty());
			cbFarbe.valueProperty().bindBidirectional(model.farbeProperty());
			tfPreis.textProperty().bindBidirectional(model.preisProperty(), new BigDecimalStringConverter());

			hideNonkeyProperty.set(false);
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
	}

	/**
	 * Save-Button.
	 */
	private void save()
	{
		try
		{
			model.save();
			cancel();
			info("Ok, Bike gesichert!");
		}
		catch (Exception e)
		{
			error(e.getMessage());
		}
	}

	/**
	 * Anzeige einer informellen Nachricht.
	 * @param msg Nachricht
	 */
	private void info(String msg)
	{
		Alert error = new Alert(Alert.AlertType.INFORMATION, msg);
		error.showAndWait();
	}

	/**
	 * Anzeige eines Fehlerss
	 * @param msg Fehlernachricht
	 */
	private void error(String msg)
	{
		Alert error = new Alert(Alert.AlertType.ERROR, msg);
		error.showAndWait();
	}
}