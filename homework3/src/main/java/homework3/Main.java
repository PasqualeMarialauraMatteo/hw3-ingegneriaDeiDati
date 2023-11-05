package homework3;

import java.io.IOException;
import java.util.Map;

import static homework3.utils.Constants.DATASET_PATH;

public class Main {
	public static void main(String[] args) throws IOException {
		//indicizzazione
		//MyJsonParser mjp = new MyJsonParser(DATASET_PATH);
		//mjp.index();
		//esempio di ricerca
		String[] queryString= { "Soccer", "TAMIU", "Complex"};
		Map<String, Integer> risultato=MySearcher.searchQuery(queryString);
		for (Map.Entry<String, Integer> entry : risultato.entrySet()) {
			String chiave = entry.getKey();
			Integer valore = entry.getValue();
			System.out.println(chiave + ": " + valore);
		}
	}
}
