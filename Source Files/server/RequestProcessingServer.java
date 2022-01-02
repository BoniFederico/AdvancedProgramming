package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import util.Logger;

public class RequestProcessingServer {

    private final int port;
    private final Processor processor;
    private final String quitCommand;
    private final ServerStat stat;
    private final Logger logger;

    public RequestProcessingServer(int port, Processor processor, String quitCommand, Logger logger) {
        this.port = port;
        this.processor = processor;
        this.quitCommand = quitCommand;
        stat = new ServerStat();
        this.logger = logger;
    }

    public void run() throws IOException, NoSuchMethodException, InstantiationException {
        try ( ServerSocket serverSocket = new ServerSocket(port)) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new ClientHandler(socket, this, logger));
            }
        }
    }

    public String process(String input) {
        return processor.process(input, stat);
    }

    public String getQuitCommand() {
        return quitCommand;
    }

}
