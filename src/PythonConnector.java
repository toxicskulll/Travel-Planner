import java.io.*;
import io.github.cdimascio.dotenv.Dotenv;

public class PythonConnector {
    public static String getAIResponse(String prompt) {
        try {
            // Create process builder for Python script
            Dotenv dotenv = Dotenv.load();
            String pyEnv = dotenv.get("PYTHON_ENV");
            String pyPath = dotenv.get("PYTHON_SCRIPT_PATH");
            ProcessBuilder pb = new ProcessBuilder(pyEnv, 
                pyPath, prompt);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read the output
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            process.waitFor();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}