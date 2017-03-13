package main;

import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.extractor.WordExtractor;

public class DocumentClassification {
	private static XWPFDocument docx;
	private static HWPFDocument doc;
	private static Scanner scn;
	private static WordExtractor extractor;

	private static Pattern affidavitsPattern;
	private static Pattern correspondencePattern;
	private static Pattern criminalLawPattern;
	private static Pattern articleAndNotesPattern;
	private static Pattern pleadingsPattern;
	private static Pattern researchMemoPattern;
	private static Pattern submissionPattern;

	private static int highestOccurence = 0;
	private static String currentCategory = "";
	final private static String EMPTY_STRING ="";

	private static int countAffidavits = 0;
	private static int countCorrespondence = 0;
	private static int countCriminalLaw = 0;
	private static int countArticleAndNotes = 0;
	private static int countPleadings = 0;
	private static int countSubmission = 0;
	private static int countResearchMemo = 0;

	public static void main(String[] args) {
		scn = new Scanner(System.in);
		initializePattern();
		while (true) {
			System.out.print("Enter filename: ");
			String fileName = scn.nextLine();
			String filePath = prepareFilePath(fileName);

			try {
				File file = new File(filePath);
				// check if it a docx file
				if (filePath.substring(filePath.length() - 1).equalsIgnoreCase("x")) {
					readDocx(file);
				} else {
					readDoc(file);
				}

			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage() + " please save as a doc/docx document");
			}

			resetStates();
		}

	}

	private static void resetStates() {
		highestOccurence = 0;
		currentCategory = "";

		countAffidavits = 0;
		countCorrespondence = 0;
		countCriminalLaw = 0;
		countArticleAndNotes = 0;
		countPleadings = 0;
		countSubmission = 0;
		countResearchMemo = 0;
	}

	private static String prepareFilePath(String fileName) {
		String userPath = System.getProperty("user.dir");
		String filePath = userPath.concat(String.join(File.separator, File.separator, "src", "main", fileName));
		return filePath;

	}

	private static void readDocx(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		docx = new XWPFDocument(fis);
		List<XWPFParagraph> paragraphs = docx.getParagraphs();
		for (int i = 0; i < paragraphs.size(); i++) {
			String para = paragraphs.get(i).getText().trim().replaceAll("\\s+", " ").toLowerCase();
			if (para.equals(EMPTY_STRING)) {
				continue;
			}
			checkPattern(para);
		}
		System.out.println("Category of this Document is : " + currentCategory);
		fis.close();
	}

	private static void checkPattern(String para) {
		matchesPattern(affidavitsPattern, para, "Affidavits");
		matchesPattern(correspondencePattern, para, "Correspondence");
		matchesPattern(criminalLawPattern, para, "Criminal Law");
		matchesPattern(articleAndNotesPattern, para, "Journal Articles And Seminar Notes");
		matchesPattern(pleadingsPattern, para, "Pleadings");
		matchesPattern(researchMemoPattern, para, "Research Memo");
		matchesPattern(submissionPattern, para, "Submission");

	}

	private static void matchesPattern(Pattern keywords, String para, String category) {
		int count = 0;
		int newMaxCount = 0;
		Matcher matcher = keywords.matcher(para);
		while (matcher.find()) {
			count++;
		}
		switch (category) {
		case "Affidavits":
			countAffidavits += count;
			newMaxCount = countAffidavits;
			break;
		case "Correspondence":
			countCorrespondence += count;
			newMaxCount = countCorrespondence;
			break;
		case "Criminal Law":
			countCriminalLaw += count;
			newMaxCount = countCriminalLaw;
			break;
		case "Journal Articles And Seminar Notes":
			countArticleAndNotes += count;
			newMaxCount = countArticleAndNotes;
			break;
		case "Pleadings":
			countPleadings += count;
			newMaxCount = countPleadings;
			break;
		case "Research Memo":
			countResearchMemo += count;
			newMaxCount = countResearchMemo;
			break;
		case "Submission":
			countSubmission += count;
			newMaxCount = countSubmission;
			break;
		}

		if (newMaxCount > highestOccurence) {
			highestOccurence = newMaxCount;
			currentCategory = category;
		}

	}

	private static void readDoc(File file) throws IOException, IllegalArgumentException {
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		doc = new HWPFDocument(fis);
		extractor = new WordExtractor(doc);
		String[] paraArr = extractor.getParagraphText();
		for (int i = 0; i < paraArr.length; i++) {
			if (paraArr[i] == EMPTY_STRING) {
				continue;
			}
			String para = paraArr[i].trim().replaceAll("\\s+", " ").toLowerCase();
			checkPattern(para);

		}
		System.out.println("Category of this Document is : " + currentCategory);
		fis.close();
	}

	private static void initializePattern() {
		affidavitsPattern = Pattern.compile("\\b(affidavit|affidavits|commissioner|oaths|affirmed at|say as follows|affirm)\\b");
		correspondencePattern = Pattern.compile("\\b(letter of demand|clientfs rights|expressly reserved|act for|instructed as follows|take notice that|take further notice that)\\b");
		criminalLawPattern = Pattern.compile("\\b(written mitigation act|charged offence|material background facts|sentencing principles|mitigating factors|charges)\\b");
		articleAndNotesPattern = Pattern.compile("\\b(general training purposes|does not constitute|seminars|journal|how|why|\\?|university|interesting question)\\b");
		pleadingsPattern = Pattern.compile("\\b(summon|amendment|relief|sought claims|statement of claim)\\b");
		researchMemoPattern = Pattern.compile("\\b(my view|wide interpretation|research memo|phrase|concluded|applicable law|i think|contemplates|this show|my judgement|i do not)\\b");
		submissionPattern = Pattern.compile("\\b(action no|section 28|paragraph 29|originating summons. 28|originating summons. 28/28f|defendant's submission|section 6.01|submissions)\\b");

	}

}
