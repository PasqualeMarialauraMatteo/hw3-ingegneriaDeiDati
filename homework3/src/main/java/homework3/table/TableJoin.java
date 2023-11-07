package homework3.table;

import homework3.MyJsonParser;

import java.io.IOException;
import java.util.Map;

import static homework3.MySearcher.searchQuery;
import static homework3.utils.Constants.DATASET_PATH;
import static homework3.utils.Utils.*;

public class TableJoin {

    public TableJoin(boolean buildIndex) {
        if (buildIndex) {
            try {
                MyJsonParser mjp = new MyJsonParser(DATASET_PATH);
                mjp.index();
            } catch (IOException e) {
                System.out.println("Unable to create the index");
            }
        }
    }

    public String[][] joinTables(int tableId, int colId) throws IOException {
        String table = findTable(DATASET_PATH, tableId);
        Map<String, Integer> results = searchQuery(getColumn(table, colId));

        if(!results.isEmpty()) {
            for (Map.Entry<String, Integer> entry: results.entrySet()) {
                String[] keys = entry.getKey().split("_");
                int matchTableId = Integer.parseInt(keys[0]);
                int matchColumnId = Integer.parseInt(keys[1]);

                // For testing
                if (matchTableId != tableId || true) {
                    return mergeTables(
                            table,
                            findTable(DATASET_PATH, matchTableId),
                            colId,
                            matchColumnId
                    );
                }
            }
        }

        return null;
    }

}
