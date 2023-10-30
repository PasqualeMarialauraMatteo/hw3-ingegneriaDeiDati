package homework3;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		MyJsonParser mjp = new MyJsonParser("C:\\Users\\paleo\\Downloads\\tables\\tables.json");
		mjp.index();
	}
}
