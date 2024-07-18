import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class CSVWriterUtil {

    public static void writeCSV(String outputFile, List<String[]> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get("output_csv", outputFile).toString()));
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
