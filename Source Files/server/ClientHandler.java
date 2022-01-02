package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import util.Logger;

public class ClientHandler implements Runnable {

    protected final Socket socket;
    protected final RequestProcessingServer server;
    protected final Logger logger;

    public ClientHandler(Socket socket, RequestProcessingServer server, Logger logger) {
        this.socket = socket;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (socket) {
            int requestCounter = 0;
            String clientAddress = socket.getInetAddress().getHostAddress();
            logger.log(Logger.LogLevel.INFORMATION, String.format("New connection from %s", clientAddress));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String line = br.readLine();
                requestCounter++;
                if (line == null) {
                    socket.close();
                    logger.log(Logger.LogLevel.WARNING, String.format("Client %s abruptly closed connection after %s request(s)", clientAddress, requestCounter));
                    break;
                }
                if (line.equals(server.getQuitCommand())) {
                    socket.close();
                    logger.log(Logger.LogLevel.INFORMATION, String.format("Client %s closed connection after %s request(s)", clientAddress, requestCounter - 1));
                    break;
                }
                if (line.equals("")) {
                    bw.write(System.lineSeparator());
                    bw.flush();
                    requestCounter--;
                    continue;
                }
                bw.write(server.process(line) + System.lineSeparator());
                bw.flush();
            }
        } catch (IOException e) {
            logger.log(Logger.LogLevel.ERROR, String.format("Failed or interrupted I/O operation on client %s connection. Error message: \"%s\"", socket.getInetAddress().getHostAddress(), e.getMessage()));

        } catch (Exception e) {
            logger.log(Logger.LogLevel.ERROR, String.format("Generic error occours on client %s connection. Error message: ", socket.getInetAddress().getHostAddress(), e.getMessage()));
        }
    }
}
