package homework3.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utils {

    private static QueryParser queryParser = new QueryParser("content", new EnglishAnalyzer());

    public static String[][] parseTable(String line) {
        JsonElement jsonTree = JsonParser.parseString(line);
        JsonObject table = jsonTree.getAsJsonObject();

        JsonArray cells = table.getAsJsonArray("cells");
        JsonObject dimention = table.get("maxDimensions").getAsJsonObject();
        int nRows = dimention.get("row").getAsInt() + 1;
        int nColumns = dimention.get("column").getAsInt() + 1;

        String[][] parsedTable = new String[nRows][nColumns];

        int cellsNumber = cells.size();
        for (int j = 0; j < cellsNumber; j++) {
            JsonObject jsonobject = cells.get(j).getAsJsonObject();

            if (!jsonobject.get("isHeader").getAsBoolean()) {
                JsonObject coordinates = jsonobject.get("Coordinates").getAsJsonObject();

                int column = Integer.parseInt(coordinates.get("column").getAsString());
                int row = Integer.parseInt(coordinates.get("row").getAsString());
                parsedTable[row][column] = jsonobject.get("cleanedText").getAsString();;
            }
        }

        return filterTable(parsedTable);
    }

    private static String[][] filterTable(String[][] parsedTable) {
        // Find empty rows
        List<Integer> emptyRows = new ArrayList<>();
        for (int i = 0; i < parsedTable.length; i++) {
            if (Arrays.stream(parsedTable[i]).allMatch(Utils::valueEmpty)) {
                emptyRows.add(i);
            }
        }

        return removeRows(parsedTable, emptyRows);
    }

    private static String[][] removeRows(String[][] originalArray, List<Integer> rowsToRemove) {
        int newSize = originalArray.length - rowsToRemove.size();
        String[][] filteredArray = new String[newSize][originalArray[0].length];

        int newIndex = 0;
        for (int i = 0; i < originalArray.length; i++) {
            if (!rowsToRemove.contains(i)) {
                filteredArray[newIndex] = originalArray[i];
                newIndex++;
            }
            else {
                rowsToRemove.remove(0);
            }
        }

        return filteredArray;
    }

    public static String[][] mergeTables(String table1, String table2, int col1, int col2) {
        String[][] parsedTable1 = parseTable(table1);
        String[][] parsedTable2 = parseTable(table2);

        List<String[]> mergedTable = new ArrayList<>();

        int nColumns = parsedTable1[0].length + parsedTable2[0].length;
        for (int i = 0; i < parsedTable1.length; i++) {
            for (int j = 0; j < parsedTable2.length; j++) {
                if (isInMatch(parsedTable1[i][col1], parsedTable2[j][col2])) {
                    String[] newRow = new String[nColumns];
                    System.arraycopy(parsedTable1[i], 0, newRow, 0, parsedTable1[0].length);
                    System.arraycopy(parsedTable2[j], 0, newRow, parsedTable1[0].length, parsedTable2[0].length);

                    mergedTable.add(newRow);
                }
            }
        }

        return convertListToArray(mergedTable);
    }

    private static boolean isInMatch(String val1, String val2) {
        try {
            return (!valueEmpty(val1) || !valueEmpty(val2)) && queryParser.parse(val1).equals(queryParser.parse(val2));
        } catch (ParseException e) {
            return false;
        }
    }

    private static String[][] convertListToArray(List<String[]> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        int numRows = list.size();
        int numCols = list.get(0).length;

        String[][] result = new String[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    private static boolean valueEmpty(String val) {
        return val == null || val.equals("") || val.equals("—");
    }

    public static void printTable(String[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.print("(" + j + ") -> ");
                System.out.print(table[i][j] + '\t');
            }
            System.out.println();
        }
        System.out.println();
    }

    public static String findTable(String datasetPath, int tableId) throws IOException {
        FileReader fileReader = new FileReader(datasetPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;
        int i = 0;
        while((line = bufferedReader.readLine()) != null) {
            if (i == tableId) {
                return line;
            }
            i++;
        }

        return null;
    }

    public static String[] getColumn(String line, int colId) {
        String[][] table = parseTable(line);
        String[] column = new String[table.length];
        for (int i = 0; i < table.length; i++) {
            column[i] = table[i][colId];
        }

        return column;
    }

}
