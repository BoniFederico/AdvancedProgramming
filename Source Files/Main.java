
import server.RequestProcessingServer;
import java.io.IOException;
import java.io.PrintStream;
import util.Logger;
import util.Logger.LogLevel;

public class Main {

    public static String QUIT_COMMAND = "BYE";
    public static PrintStream LOG_PRINT_STREAM = System.out;

    public static void main(String[] args) throws IOException {
        Logger logger = new Logger(LOG_PRINT_STREAM);
        int port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.log(LogLevel.ERROR, "Invalid port number");
            return;
        } catch (IndexOutOfBoundsException e) {
            logger.log(LogLevel.ERROR, "Please provide a port number");
            return;
        }
        try {
            RequestProcessingServer s = new RequestProcessingServer(port, new RequestProcessor(), QUIT_COMMAND, logger);
            s.run();
        } catch (IOException e) {
            logger.log(LogLevel.ERROR, String.format("Failed or interrupted I/O operation. Error message: \"%s\"", e.getMessage()));
        } catch (Exception e) {
            logger.log(LogLevel.ERROR, String.format("Generic error occours. Error message: \"%s\"", e.getMessage()));

        }

    }

}
