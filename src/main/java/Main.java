import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.NoSuchElementException;

public class Main {

	private static final String pathToFiles = "pdfs";
	private static final int port = 8989;
	private static final String fileName = "searchingResults.json";

	public static void main(String[] args) throws Exception {

		BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
		System.out.println(engine.search("бизнес"));

		// здесь создайте сервер, который отвечал бы на нужные запросы
		// слушать он должен порт 8989
		// отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате

		serverSearch(pathToFiles);
	}

	private static void serverSearch(String pathToFiles) throws Exception {

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			while (true) {
				try (Socket socket = serverSocket.accept();
					 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

					out.println("Write a word: ");

					String wordToFind;
					while ((wordToFind = in.readLine()).matches("[^[:alpha:]]")) {
						out.println("Write a word: ");
					}

					out.println(programSearching(pathToFiles, wordToFind));
					out.println("Goodbye");
					break;

				} catch (IOException | NoSuchElementException ex) {
					ex.printStackTrace();
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static Object programSearching(String pathToFiles, String wordToFind) throws Exception {
		BooleanSearchEngine booleanSearchEngine = new BooleanSearchEngine(new File(pathToFiles));
		List<PageEntry> results = booleanSearchEngine.search(wordToFind);
		String stringResults = listToJson(results);
		writeFileResults(stringResults);
		return stringResults;
	}

	private static String listToJson(List<PageEntry> list) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder
				.setPrettyPrinting()
				.create();
		Type listType = new TypeToken<List<PageEntry>>() {
		}.getType();
		String showResults = gson.toJson(list, listType);
		System.out.println(showResults);
		return showResults;
	}

	private static void writeFileResults(String json) {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(json);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
