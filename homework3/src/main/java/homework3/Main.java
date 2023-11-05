package homework3;

import static homework3.utils.Constants.DATASET_PATH;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
	public static void main(String[] args) throws IOException {
		//indicizzazione
//		MyJsonParser mjp = new MyJsonParser(DATASET_PATH);
//		mjp.index();
		//esempio di ricerca
//		String[] queryString= { "Soccer", "TAMIU", "Complex"};
//		Map<String, Integer> risultato=MySearcher.searchQuery(queryString);
//		for (Map.Entry<String, Integer> entry : risultato.entrySet()) {
//			String chiave = entry.getKey();
//			Integer valore = entry.getValue();
//			System.out.println(chiave + ": " + valore);
//		}
		
		//Calcolo statistiche
		try {
            // Specifica il nome del file di destinazione
            String fileName = "statistiche.txt";
            // Crea un nuovo oggetto FileOutputStream per il file di destinazione
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            // Crea un oggetto PrintStream che punta al file di destinazione
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.out.println("CALCOLO STATISTICHE IN CORSO...");
            // Salva l'output corrente in un backup per eventualmente ripristinarlo in seguito
            PrintStream originalOut = System.out;
            // Imposta System.out per utilizzare il PrintStream per l'output
            System.setOut(printStream);
            
            // Ora tutto ciò che viene stampato con System.out verrà ridirezionato nel file "output.txt"
    		Statistics statisticCalculator = new Statistics(DATASET_PATH);
    		statisticCalculator.calculateStatistics();
    		statisticCalculator.printStatistics();
    		
            // Ripristina l'output originale (la console)
            System.setOut(originalOut);
            System.out.println("CALCOLO TERMINATO.");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
}
