package homework3;

import java.io.IOException;

import static homework3.utils.Constants.DATASET_PATH;

public class Main {
	public static void main(String[] args) throws IOException {
		MyJsonParser mjp = new MyJsonParser(DATASET_PATH);
		mjp.index();
	}
}
