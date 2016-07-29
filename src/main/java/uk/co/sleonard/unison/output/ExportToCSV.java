/**
 * ExportToCSV
 *
 * @author Stephen <github@leonarduk.com>
 * @since 22-May-2016
 */
package uk.co.sleonard.unison.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.co.sleonard.unison.UNISoNControllerFX;
import uk.co.sleonard.unison.UNISoNException;
import uk.co.sleonard.unison.gui.PajekPanelFX.MatrixModelTableView;

/**
 * The Class ExportToCSV.
 *
 * @author Stephen <github@leonarduk.com>
 * @since v1.0.0
 *
 */
public class ExportToCSV {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		final ExportToCSV test = new ExportToCSV();
		final String data = "M'I-5'Persecut ion , Bern ard Le vin expre sses h is v iews";
		test.extractCommas(data);
	}

	/**
	 * Export table.
	 *
	 * @param fileName
	 *            the file name
	 * @param table
	 *            the table
	 * @param fieldNames
	 *            the field names
	 * @throws UNISoNException
	 *             the UNI so n exception
	 */
	public void exportTable(final String fileName, final TableView<MatrixModelTableView> table) throws UNISoNException {
		try {
			final File file = new File(fileName);
			if (file != null) {

				// clear old file if it exists
				if (file.exists()) {
					file.delete();
				}
				try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
						final PrintWriter fileWriter = new PrintWriter(bufferedWriter);) {
					String data;
					for (int j = 0; j < table.getColumns().size(); ++j) {

						// replace any commas in data!
						data = this.extractCommas(table.getColumns().get(j).getText());

						fileWriter.print(data + ",");
					}
					fileWriter.println("");
					for (int i = 0; i < table.getItems().size(); ++i) {
						MatrixModelTableView model = table.getItems().get(i);
						StringBuffer stb = new StringBuffer();
						stb.append(model.getSubject()).append(",").append(model.getDate()).append(",")
								.append(model.getFrom()).append(",").append(model.getTo());
						fileWriter.print(stb.toString());
						fileWriter.println("");
					}
					fileWriter.close();
				} catch (final Exception e) {
					Platform.runLater(() -> {
						UNISoNControllerFX.getGui().showAlert("Error " + e);
					});
				}
			}

		} catch (final Exception e) {
			throw new UNISoNException("Failed to export to CSV", e);
		}
	}// export
		// Table

	/**
	 * Export table to csv.
	 *
	 * @param table
	 *            the table
	 * @throws UNISoNException
	 *             the UNI so n exception
	 */
	public void exportTableToCSV(final TableView<MatrixModelTableView> table, Stage stage) throws UNISoNException {
		final FileChooser file = new FileChooser();
		file.setTitle("Save CSV Network File");
		final String CSV_FILE_SUFFIX = ".csv";
		final String initialValue = "*" + CSV_FILE_SUFFIX;
		file.setInitialFileName(initialValue); // set initial filename filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Comma-separated values (.csv)",
				"*" + CSV_FILE_SUFFIX);
		file.getExtensionFilters().add(extFilter);
		File archive = file.showSaveDialog(stage);
		// file.show(); // Blocks
		String curFile = null;
		curFile = archive.getName();
		if ((curFile != null) && !curFile.equals(initialValue)) {

			if (!curFile.endsWith(CSV_FILE_SUFFIX)) {
				curFile += CSV_FILE_SUFFIX;
			}
			final String filename = archive.getAbsolutePath() + curFile;
			this.exportTable(filename, table);
		}

	}

	/**
	 * Extract commas.
	 *
	 * @param data
	 *            the data
	 * @return the string
	 */
	private String extractCommas(final String dataInput) {
		if (dataInput.indexOf(',') > -1) {
			return dataInput.replaceAll(",", ";");
		}
		return dataInput;
	}

}
