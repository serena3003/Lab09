/**
 * Skeleton for 'Borders.fxml' Controller Class
 */

package it.polito.tdp.borders;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class BordersController {

	Model model;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="txtAnno"
	private TextField txtAnno; // Value injected by FXMLLoader

	@FXML
	private ComboBox<Country> cboxCountry;

	@FXML
	private Button btnVicini;

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML
	void doCalcolaConfini(ActionEvent event) {

		int anno;

		try {
			anno = Integer.parseInt(txtAnno.getText());
			if ((anno < 1816) || (anno > 2016)) {
				txtResult.setText("Inserire un anno nell'intervallo 1816 - 2016");
				return;
			}

		} catch (NumberFormatException e) {
			txtResult.setText("Inserire un anno nell'intervallo 1816 - 2016");
			return;
		}

		try {
			model.creaGrafo(anno);
			txtResult.appendText(model.calcolaVicini());
			txtResult.appendText("\nNumero di componenti connesse del grafo: " + model.getNumberOfConnectedComponents());

		} catch (RuntimeException e) {
			txtResult.setText("Errore: " + e.getMessage() + "\n");
			return;
		}
	}

	@FXML
	void doCercaVicini(ActionEvent event) {
		txtResult.clear();
		Country c = cboxCountry.getValue();
		try {
			List<Country> vicini = model.cercaVicini(c);
			txtResult.appendText("I nodi raggiungibili da " + c.getAbb() + " sono : \n " + vicini);
			
			List<Country> viciniIter = model.cercaViciniIter(c);
			txtResult.appendText("\nI nodi raggiungibili da " + c.getAbb() + " sono : \n " + viciniIter);
			
			/*boolean equa = true;
			for(Country c1 : vicini)
				if(!viciniIter.contains(c1))
					equa = false;
			System.out.println(equa);*/
			

		} catch (RuntimeException e) {
			// If the countries are inserted in the ComboBox when the graph is created,
			// this should never happen.
			txtResult.setText("Selected country is not in the graph.");
		}
		
		
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'Borders.fxml'.";
		assert cboxCountry != null : "fx:id=\"cboxCountry\" was not injected: check your FXML file 'Borders.fxml'.";
		assert btnVicini != null : "fx:id=\"btnVicini\" was not injected: check your FXML file 'Borders.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Borders.fxml'.";
	}

	public void setModel(Model model2) {
		this.model = model2;

		for (Country c : model.getCountryList()) {
			cboxCountry.getItems().addAll(c);
		}
	}
}
