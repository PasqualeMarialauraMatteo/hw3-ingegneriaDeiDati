package homework3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Statistics {

	private FileReader fileReader;
	private BufferedReader bufferedReader;
	private int tableNumber;
	private float meanTableRows;
	private float meanTableColumns;
	private float meanNullValues;
	private HashMap<Integer, Integer> rowsToTableNumber;
	private HashMap<Integer, Integer> columnsToTableNumber;
	private HashMap<Integer, Integer> distValuesToColumns;

	public Statistics(String filePath) throws FileNotFoundException {
		this.fileReader = new FileReader(filePath);
		this.bufferedReader = new BufferedReader(fileReader);
		this.tableNumber = 0;
		this.meanNullValues = 0;
		this.meanTableColumns = 0;
		this.meanTableRows = 0;
		this.rowsToTableNumber = new HashMap<Integer, Integer>();
		this.columnsToTableNumber = new HashMap<Integer, Integer>();
		this.distValuesToColumns = new HashMap<Integer, Integer>();
	}


	public void calculateStatistics() throws IOException {
		System.out.println("CALCOLO STATISTICHE AVVIATO");
		calculate();
	}

	public void printStatistics() {
		System.out.println("======================================================================");
		System.out.println("Numero di tabelle nel dataset: " + this.tableNumber);
		System.out.println("Numero medio di righe per tabella: " + this.meanTableRows);
		System.out.println("Numero medio di colonne per tabella: " + this.meanTableColumns);
		System.out.println("Numero medio di valori nulli per tabella: " + this.meanNullValues);
		//stampiamo ora la distribuzione del nuemro di righe e di colonne
		System.out.println("======================================================================");
		System.out.println("Distribuzuione righe:");
		Map<Integer, Integer> sortedRowsMap = new TreeMap<>(this.rowsToTableNumber);
		for (Map.Entry<Integer, Integer> entry : sortedRowsMap.entrySet()) {
			Integer numeroRighe = entry.getKey();
			Integer numeroTabelle = entry.getValue();
			System.out.println(numeroTabelle + " tabelle hanno " + numeroRighe + " righe.");
		}
		System.out.println("**********************************************************************");
		System.out.println("Distribuzuione colonne:");
		Map<Integer, Integer> sortedColumnsMap = new TreeMap<>(this.columnsToTableNumber);
		for (Map.Entry<Integer, Integer> entry : sortedColumnsMap.entrySet()) {
			Integer numeroColonne = entry.getKey();
			Integer numeroTabelle = entry.getValue();
			System.out.println(numeroTabelle + " tabelle hanno " + numeroColonne + " colonne.");
		}
		System.out.println("**********************************************************************");
		System.out.println("Distribuzuione valori distinti :");
		Map<Integer, Integer> sortedDistMap = new TreeMap<>(this.distValuesToColumns);
		for (Map.Entry<Integer, Integer> entry : sortedDistMap.entrySet()) {
			Integer valoriDistinti = entry.getKey();
			Integer numeroColonne = entry.getValue();
			System.out.println(numeroColonne + " colonne hanno " + valoriDistinti + " valori distinti.");
		}
	}

	public void calculate() throws IOException {
		this.bufferedReader = new BufferedReader(this.fileReader);
		int tableNum;
		int totalRows = 0;
		int totalColumns = 0;
		int totalNullValues = 0;
		String line = null;		//stringa per leggere ogni linea di table.json
		for (tableNum = 0 ;; tableNum++) {
			line = this.bufferedReader.readLine();
			//se la line è vuota la tabella è finita e bisogna passare alla successiva
			if (line == null) break;
			JsonElement jsonTree = JsonParser.parseString(line);
			JsonObject table = jsonTree.getAsJsonObject();
			JsonObject dimensions = table.get("maxDimensions").getAsJsonObject();
			// rughe e colonne della tabella vista al momento
			int actualRows = dimensions.get("row").getAsInt();
			int actualColumns = dimensions.get("column").getAsInt();
			totalRows += actualRows;
			totalColumns += actualColumns;
			// collezioniamo il numero di tabelle che hanno tot colonne e tot righe
			this.rowsToTableNumber.put(actualRows, this.rowsToTableNumber.getOrDefault(actualRows, 0) + 1);
			this.columnsToTableNumber.put(actualColumns, this.columnsToTableNumber.getOrDefault(actualColumns, 0) + 1);

			JsonArray cells = table.getAsJsonArray("cells");
			int cellsNumber = cells.size();
			//mappa dove memorizzo le celle distinte in una lista per colonna
			Map<String, HashSet<String>> colonnaToCella = new HashMap<>();
			for (int j = 0; j < cellsNumber; j++) {

				JsonObject jsonobject = cells.get(j).getAsJsonObject();
				//se non è un header è contenuto di interesse
				if (jsonobject.get("type").getAsString().equals("EMPTY"))
					totalNullValues++;
				else if (!jsonobject.get("isHeader").getAsBoolean()) {
					JsonObject coordinates = jsonobject.get("Coordinates").getAsJsonObject();
					String column = coordinates.get("column").getAsString();
					String cell = jsonobject.get("cleanedText").getAsString();
					if(colonnaToCella.containsKey(column)) {
						colonnaToCella.get(column).add(cell);
					}
					else {
						colonnaToCella.put(column, new HashSet<String>());
					}
				}

			}
			for(String col : colonnaToCella.keySet()) {
				this.distValuesToColumns.put(colonnaToCella.get(col).size(), this.distValuesToColumns.getOrDefault(colonnaToCella.get(col).size(), 0) + 1);
			}
 
		}

		this.tableNumber = tableNum;
		this.meanTableRows = (float)totalRows/this.tableNumber;
		this.meanTableColumns = (float)totalColumns/this.tableNumber;
		this.meanNullValues = (float)totalNullValues/this.tableNumber;
	}

}
