package homework3;

import static homework3.utils.Constants.CONTENT;
import static homework3.utils.Constants.INDEX_PATH;
import static homework3.utils.Constants.TABLE_COLUMN;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MySearcher {

	//metodo che data una query ritorna una mappa con le colonne ordinate in base agli hit
	public static Map<String, Integer> searchQuery(String[] queryString) {
		
		//mappa che tiene conto di quanti hit abbiamo per colonna
		Map<String, Integer> columnOccurrences = new HashMap<>();
		//per determinare il tempo di reicerca per ogni termine 
		long startTime = 0;
		long endTime = 0;

		try (Directory directory = FSDirectory.open(Paths.get(INDEX_PATH))) {
			
			//creazione del searcher
			IndexReader reader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			for(String s : queryString) {
				
				TermQuery termQuery = new TermQuery(new Term(CONTENT, s.toLowerCase()));
				System.out.println(s);
				System.out.println("Sto cercando...");
				
				//tempo di ricerca nell'indice
				startTime = System.currentTimeMillis();
				queryRunner(searcher, termQuery, columnOccurrences);
				endTime = System.currentTimeMillis();
				System.out.println("Tempo ricerca: " + (endTime-startTime)/1000 + "secondi");
			}
		} catch (IOException e) {
			// Gestione delle eccezioni
			e.printStackTrace();
		}
		
		//ritono della mappa ordinata per valore in modo da tornare le colonne colonne in ordine di similarità alla query
		return sortingMap(columnOccurrences);
	}


	//funzione che ordina gli elementi della mappa in accordo ai valori
	private static HashMap<String, Integer> sortingMap(Map<String, Integer> columnOccurrences) {
		//Crea una lista dagli elementi della mappa
		List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(columnOccurrences.entrySet());

		// Ordina la lista in base ai valori
		list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		// Crea una mappa ordinata
		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> l : list) {
			sortedMap.put(l.getKey(), l.getValue());
		}

		return sortedMap;
	}

	//funzione principale per la ricerca
	private static void queryRunner(IndexSearcher searcher, Query query,  Map<String, Integer> columnOccurrences) throws IOException {
		int totalColumns = searcher.count(query);
		Set<String> columns = new HashSet<>();
		System.out.println(totalColumns);

		if(totalColumns > 0) {
			
			TopDocs hits = searcher.search(query, totalColumns);
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				
				ScoreDoc scoreDoc = hits.scoreDocs[i];
				Document doc = searcher.doc(scoreDoc.doc);

				columns.add(doc.get(TABLE_COLUMN));
			}

			// Aggiorna le occorrenze delle colonne nella mappa
			for (String c : columns) {
				columnOccurrences.put(c, columnOccurrences.getOrDefault(c, 0) + 1);
			}
		}
	}

}
