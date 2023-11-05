package homework3.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

    public static List<List<String>> parseTable(String line) {
        JsonElement jsonTree = JsonParser.parseString(line);
        JsonObject table = jsonTree.getAsJsonObject();

        JsonArray cells = table.getAsJsonArray("cells");
        int dim = table.get("maxDimensions").getAsJsonObject().get("column").getAsInt() + 1;

        List<List<String>> parsedTable = IntStream.range(0, dim)
                .mapToObj(i -> new ArrayList<String>())
                .collect(Collectors.toList());

        int cellsNumber = cells.size();
        for (int j = 0; j < cellsNumber; j++) {
            JsonObject jsonobject = cells.get(j).getAsJsonObject();

            if (!jsonobject.get("isHeader").getAsBoolean()) {
                JsonObject coordinates = jsonobject.get("Coordinates").getAsJsonObject();
                int column = Integer.parseInt(coordinates.get("column").getAsString());
                String cell = jsonobject.get("cleanedText").getAsString();
                parsedTable.get(column).add(cell);
            }
        }

        return parsedTable;
    }

}
