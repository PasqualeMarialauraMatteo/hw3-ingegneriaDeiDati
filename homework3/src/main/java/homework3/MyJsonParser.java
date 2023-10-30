package homework3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class MyJsonParser {

	private FileReader fileReader;
	private BufferedReader bufferedReader;

	public MyJsonParser(String filePath) throws IOException {
		this.fileReader = new FileReader(filePath);
		this.bufferedReader = new BufferedReader(fileReader);
	}

	public void index() throws IOException {

		//definizione analyzer
		Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
		perFieldAnalyzers.put("tabella_colonna", new StandardAnalyzer());
		perFieldAnalyzers.put("contenuto", new StandardAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(),perFieldAnalyzers);
		Path path = Paths.get("C:\\Users\\paleo\\git\\hw3-ingegneriaDeiDati\\homework3\\index");
		Directory directory = FSDirectory.open(path);
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(directory, config);
		config.setCodec(new SimpleTextCodec());
		writer.deleteAll();

		long startTime = System.currentTimeMillis();
		String line = null;		//stringa per leggere ogni linea di table.json

		for (int tableNumber = 0;; tableNumber++) {
			line = this.bufferedReader.readLine();
			//se la line è vuota la tabella è finita e bisogna passare alla successiva
			if (line == null) break;

			JsonElement jsonTree = JsonParser.parseString(line);
			JsonObject table = jsonTree.getAsJsonObject();

			Document doc = new Document();
			
			//mappa dove memorizzo le celle per colonna
			Map<String, String> colonnaToCella = new HashMap<>();

			JsonArray cells = table.getAsJsonArray("cells");
			int cellsNumber = cells.size();
//			String oldcell = null;
			for (int j = 0; j < cellsNumber; j++) {

				JsonObject jsonobject = cells.get(j).getAsJsonObject();
				//se non è un header è contenuto di interesse
				if (!jsonobject.get("isHeader").getAsBoolean()){
					JsonObject coordinates = jsonobject.get("Coordinates").getAsJsonObject();
					String column = coordinates.get("column").getAsString();
					String cell = jsonobject.get("cleanedText").getAsString();
					if (!cell.equals("")/*&& !cell.equals(oldcell)*/) {
						if(colonnaToCella.containsKey(column)) {
							colonnaToCella.put(column, colonnaToCella.get(column) + " " + cell);
						}
						else {
							colonnaToCella.put(column, cell);
						}
					}
					//oldcell = cell; 
				}
			}
			
			if(!colonnaToCella.isEmpty()) {
				for(String col : colonnaToCella.keySet()) {
					doc.add(new TextField("tabella_colonna", Integer.toString(tableNumber)+"_"+col, Field.Store.YES));
					doc.add(new TextField("contenuto",colonnaToCella.get(col), Field.Store.YES));
					if(tableNumber == 0)
						System.out.println(doc);
					writer.commit();
				}
			}
						
		}
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println("Tempo di indicizzazione: " + (endTime-startTime)/1000 + " secondi" );

		this.bufferedReader.close();
		this.fileReader.close();
	}

}

