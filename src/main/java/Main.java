import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;

public class Main {

	private static final int port = 8989;
	private static final String hostName = "localhost";
	private static final String fileName = "searchingResults.json";

	public static void main(String[] args) throws Exception {

//		BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
//		System.out.println(engine.search("бизнес"));

		String pathToFiles = "pdfs";
		serverSearch(pathToFiles);

		// здесь создайте сервер, который отвечал бы на нужные запросы
		// слушать он должен порт 8989
		// отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате

	}

	private static void serverSearch(String pathToFiles) throws Exception {

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			while (true) {

				//client
				try (Socket client = new Socket(hostName, port);
					 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
					 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

					System.out.println("Hello");
					System.out.println("Enter a word:");
					String word;

					while ((word = reader.readLine()).equals("")) {
						System.out.println("Enter a word:");
					}

					if (word.equals(".")) {
						System.out.println("You interrupted program");
						break;
					} else {
						out.println(word);
					}

				} catch (IOException ex) {
					System.out.println("Server was closed");
				}

				//server
				try (Socket socket = serverSocket.accept();
					 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

					String wordToFind = in.readLine();
					System.out.println("You are looking for word: " + wordToFind);
					System.out.println("Searching results:");

					//searching
					programSearching(pathToFiles, wordToFind);

					System.out.println("Good bye");
					break;

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void programSearching(String pathToFiles, String wordToFind) throws Exception {
		BooleanSearchEngine booleanSearchEngine = new BooleanSearchEngine(new File(pathToFiles));
		List<PageEntry> results = booleanSearchEngine.search(wordToFind);
		System.out.println(results);
		writeString(listToJson(results));
		openFile();
	}

	private static <T> String listToJson(List<PageEntry> list) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder
				.setPrettyPrinting()
				.create();
		Type listType = new TypeToken<List<T>>() {
		}.getType();
		return gson.toJson(list, listType);
	}

	private static void writeString(String json) {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(json);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void openFile() throws Exception {
		File file = new File(fileName);
		if (!Desktop.isDesktopSupported()) {
			System.out.println("Desktop is not supported");
		}
		Desktop desktop = Desktop.getDesktop();
		if (file.exists()) {
			desktop.open(file);
		}
	}

}
