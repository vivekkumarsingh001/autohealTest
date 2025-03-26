package common;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFUtil {
	
	static final Logger log = Logger.getLogger(PDFUtil.class);

	public static String getText(String file) throws IOException {
		String extention = FilenameUtils.getExtension(file);
		File pdfFile;
		if (extention != null && !extention.trim().isEmpty()) {
			pdfFile = new File(file);
		} else {
			pdfFile = getTheNewestFile(file, "pdf");
		}

		PDDocument doc = PDDocument.load(pdfFile);
		return new PDFTextStripper().getText(doc);
	}

	/* Get the newest file for a specific extension */
	static File getTheNewestFile(String filePath, String ext) {
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			log.error(e);
		}
		File neweFile = null;
		File dir = new File(filePath);
		FileFilter fFilter = new WildcardFileFilter("*." + ext);
		File[] fileList = dir.listFiles(fFilter);

		if (fileList.length > 0) {
			/** The newest file comes first **/
			Arrays.sort(fileList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			neweFile = fileList[0];
		}

		return neweFile;
	}

}
