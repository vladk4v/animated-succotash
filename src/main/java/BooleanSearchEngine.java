import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

	//???

	private Map<String, List<PageEntry>> vocabulary = new TreeMap<>();

	private final FileFilter filter = pathname -> pathname.getName().endsWith(".pdf");


	public BooleanSearchEngine(File pdfsDir) throws IOException {

		// прочтите тут все pdf и сохраните нужные данные,
		// тк во время поиска сервер не должен уже читать файлы

		for (File item : pdfsDir.listFiles(filter)) {
			if (pdfsDir.isDirectory()) {
				String nameOfFile = item.getName();
				String pathToFile = item.getPath();

				var guineaPig = new PdfDocument(new PdfReader(pathToFile));
				int numberOfPages = guineaPig.getNumberOfPages();

				for (int pageNumber = 1; pageNumber < numberOfPages; pageNumber++) {

					var text = PdfTextExtractor.getTextFromPage(guineaPig.getPage(pageNumber));
					String[] wordStock = text.split("\\P{IsAlphabetic}+");

					Map<String, Integer> wordStat = new TreeMap<>();

					for (var word : wordStock) {
						if (word.isEmpty()) {
							continue;
						}
						wordStat.put(word.toLowerCase(), wordStat.getOrDefault(word.toLowerCase(), 0) + 1);
					}

					//saving results
					for (Map.Entry<String, Integer> entry : wordStat.entrySet()) {
						String word = entry.getKey();
						Integer counts = entry.getValue();
						if (vocabulary.containsKey(word)) {
							List<PageEntry> newbie = vocabulary.get(word);
							newbie.add(newbie.size(), new PageEntry(nameOfFile, pageNumber, counts));
							newbie.sort(Comparator.reverseOrder());
							vocabulary.replace(word, newbie);
						} else {
							List<PageEntry> oldie = new ArrayList<>();
							oldie.add(0, new PageEntry(nameOfFile, pageNumber, counts));
							vocabulary.put(word, oldie);
						}
					}
				}
			}
		}
	}


	@Override
	public List<PageEntry> search(String word) {

		// тут реализуйте поиск по слову

		List<PageEntry> searchingResults = vocabulary.entrySet().stream()
				.filter(page -> word.equals(page.getKey()))
				.map(Map.Entry::getValue)
				.iterator()
				.next();

		//		return Collections.emptyList();

		return searchingResults;
	}
}





