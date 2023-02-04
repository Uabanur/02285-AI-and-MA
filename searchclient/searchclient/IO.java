package searchclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

enum LogLevel {
    Debug, Information, Warning, Error
}

class IO {

    static Boolean debugServerMessages = false;
    static LogLevel logLevel = LogLevel.Information;

    private static BufferedReader serverMessages = new BufferedReader(
        new InputStreamReader(System.in, StandardCharsets.US_ASCII));

    private static void sendToServerRaw(String msg){
        System.out.println(msg);
    }
    private static void log(String msg){
        System.err.println("[client]"+msg);
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
    R apply(T t) throws IOException;
    }

    static State initializeServerCommunication(String name, CheckedFunction<BufferedReader, State> parseLevelFunction) 
    throws IOException{
        sendToServerRaw(name);
        info("Client name: " + name);
        return parseLevelFunction.apply(serverMessages);
    }

    static void debug(String msg){
        if (logLevel == LogLevel.Debug){
            log("[debug] "+msg);
        }
    }
    static void debug(String format, Object... args){
        debug(String.format(format, args));
    }

    static void info(String msg){
        if (logLevel == LogLevel.Information || logLevel == LogLevel.Debug) {
            log("[info] "+msg);
        }
    }
    static void info(String format, Object... args){
        info(String.format(format, args));
    }

    static void warn(String msg){
        if (logLevel == LogLevel.Error) return;
        log("[warn] "+msg);
    }
    static void warn(String format, Object... args){
        warn(String.format(format, args));
    }

    static void error(String msg){
        log("[error] " +msg);
    }
    static void error(String format, Object... args){
        error(String.format(format, args));
    }

    static void sendPlanToServer(Action[][] plan) throws IOException{
        for (Action[] jointAction : plan)
        {
            String strategy = String.join("|", 
                Stream.of(jointAction).map(a -> a.name).toArray(String[]::new));

            sendToServerRaw(strategy);

            if (debugServerMessages){
                debug("Strategy: " + strategy);
            }

            String response = serverMessages.readLine();

            if (debugServerMessages){
                debug("Strategy response: " + response);
            }
        }
    }
}
