import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SurveyDataProcessor {

    public static void main(String[] args) {
        String[] inputFiles = {
                "AOTF SURVEYS- NO B&F/SITE1_PREIMP_RAW.csv",
                "AOTF SURVEYS- NO B&F/SITE2_PREIMP_RAW.csv",
                "AOTF SURVEYS- NO B&F/SITE3_PREIMP_RAW.csv",
                "AOTF SURVEYS- NO B&F/SITE4_PREIMP_RAW.csv"
        };

        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a site to process (1-4):");
        int siteIndex = scanner.nextInt() - 1;

        if (siteIndex >= 0 && siteIndex < inputFiles.length) {
            List<String[]> data = CSVReaderUtil.readFile(inputFiles[siteIndex]);
            String baseOutputFileName = "SITE" + (siteIndex + 1) + "_";
            List<String[]> transposedData = transposeData(data);
            separateData(transposedData,
                    baseOutputFileName + "acceptability_output.csv",
                    baseOutputFileName + "appropriateness_output.csv",
                    baseOutputFileName + "feasibility_output.csv");
        } else {
            System.out.println("Invalid site selection.");
        }

        scanner.close();
    }

    private static void separateData(List<String[]> transposedData, String outputFileAcceptability, String outputFileAppropriateness, String outputFileFeasibility) {
        System.out.println("Separating data into categories...");

        List<String[]> acceptabilityData = new ArrayList<>();
        List<String[]> appropriatenessData = new ArrayList<>();
        List<String[]> feasibilityData = new ArrayList<>();

        // Adding headers
        acceptabilityData.add(new String[]{"record_id", "acceptability_approval", "acceptability_appealing", "acceptability_like", "acceptability_welcome"});
        appropriatenessData.add(new String[]{"record_id", "appropriateness_fitting", "appropriateness_suitable", "appropriateness_applicable", "appropriateness_goodmatch"});
        feasibilityData.add(new String[]{"record_id", "feasibility_implement", "feasibility_possible", "feasibility_doable", "feasibility_easy"});

        int recordId = 1;
        int numColumns = transposedData.get(0).length;
        for (int i = 1; i < transposedData.size(); i++) {  // Skip the first row (headers)
            String[] row = transposedData.get(i);
            System.out.println("Processing row: " + String.join(",", row));

            boolean isValidAcceptability = isValidCategoryEntry(row, 1, 4);
            boolean isValidAppropriateness = isValidCategoryEntry(row, 5, 8);
            boolean isValidFeasibility = isValidCategoryEntry(row, 9, 12);

            if (isValidAcceptability) {
                acceptabilityData.add(new String[]{String.valueOf(recordId), row[1], row[2], row[3], row[4]});
            } else {
                System.out.println("Skipping invalid acceptability row: " + String.join(",", row));
            }

            if (isValidAppropriateness) {
                appropriatenessData.add(new String[]{String.valueOf(recordId), row[5], row[6], row[7], row[8]});
            } else {
                System.out.println("Skipping invalid appropriateness row: " + String.join(",", row));
            }

            if (isValidFeasibility) {
                feasibilityData.add(new String[]{String.valueOf(recordId), row[9], row[10], row[11], row[12]});
            } else {
                System.out.println("Skipping invalid feasibility row: " + String.join(",", row));
            }

            if (isValidAcceptability || isValidAppropriateness || isValidFeasibility) {
                recordId++;
            }
        }

        System.out.println("Writing acceptability data to " + outputFileAcceptability);
        CSVWriterUtil.writeCSV(outputFileAcceptability, acceptabilityData);

        System.out.println("Writing appropriateness data to " + outputFileAppropriateness);
        CSVWriterUtil.writeCSV(outputFileAppropriateness, appropriatenessData);

        System.out.println("Writing feasibility data to " + outputFileFeasibility);
        CSVWriterUtil.writeCSV(outputFileFeasibility, feasibilityData);
    }

    private static boolean isDataRow(String[] row, int numColumns) {
        // Check if the row has the correct number of columns and is not a header row
        return row.length == numColumns && !row[0].equals("record_id");
    }

    private static boolean isValidCategoryEntry(String[] row, int startCol, int endCol) {
        for (int i = startCol; i <= endCol; i++) {
            if (i >= row.length || row[i] == null || row[i].trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<String[]> transposeData(List<String[]> data) {
        System.out.println("Transposing data...");
        int numRows = data.size();
        int numCols = data.get(0).length;
        List<String[]> transposedData = new ArrayList<>();

        for (int i = 0; i < numCols; i++) {
            String[] transposedRow = new String[numRows];
            for (int j = 0; j < numRows; j++) {
                transposedRow[j] = data.get(j)[i];
            }
            transposedData.add(transposedRow);
        }

        System.out.println("Transposition complete. Number of rows: " + transposedData.size());
        return transposedData;
    }
}
