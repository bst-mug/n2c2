package at.medunigraz.imi.bst.n2c2.nn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

/**
 * Refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class DataUtilities {

	private static final int BUFFER_SIZE = 4096;

	public static void extractTarGz(String filePath, String outputPath) throws IOException {
		int fileCount = 0;
		int dirCount = 0;
		System.out.print("Extracting files");

		try (TarArchiveInputStream tais = new TarArchiveInputStream(
				new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(filePath))))) {
			TarArchiveEntry entry;

			while ((entry = (TarArchiveEntry) tais.getNextEntry()) != null) {

				// create directories
				if (entry.isDirectory()) {
					new File(outputPath + entry.getName()).mkdirs();
					dirCount++;
				} else {
					int count;
					byte data[] = new byte[BUFFER_SIZE];

					FileOutputStream fos = new FileOutputStream(outputPath + entry.getName());
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
					while ((count = tais.read(data, 0, BUFFER_SIZE)) != -1) {
						dest.write(data, 0, count);
					}
					dest.close();
					fileCount++;
				}
				if (fileCount % 1000 == 0)
					System.out.print(".");
			}
		}
		System.out.println("\n" + fileCount + " files and " + dirCount + " directories extracted to: " + outputPath);
	}

	public List<String> getSentences(String narrative) {
		String abbreviations = "\\d|Mr|Dr|Drs|Ms|c|C";

		String cleanedNarrative = "";
		String tempString = "";

		// cleansing beginning input lines
		try {
			List<String> lines = IOUtils.readLines(new StringReader(narrative));
			for (String line : lines) {
				if (line.length() > 0) {
					tempString = line.trim();
					if (tempString.length() > 0)
						cleanedNarrative += tempString.replaceAll("[\t\\*\\*_\\%]+", " ").trim() + "\n";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// new line split logic
		String[] splits = cleanedNarrative.split("\n(?=[A-Z]|[0-9])");
		ArrayList<String> sentences = new ArrayList<String>();

		// period character split logic
		for (String split : splits) {
			split = split.replaceAll("[\r\n\\s]+", " ").trim();
			split = split.replaceAll("\\.+", ".").trim();
			if (split.length() > 0) {
				sentences.addAll(Arrays.asList(split.split("(?<!" + abbreviations + ")(\\.)(\\s+)")));
			}
		}

		// post cleaning
		// TODO

		sentences.forEach(sentence -> System.out.println(sentence.trim()));
		return sentences;
	}
}
