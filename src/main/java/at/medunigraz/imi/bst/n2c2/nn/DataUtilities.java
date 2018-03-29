package at.medunigraz.imi.bst.n2c2.nn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

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
}
