package api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class FileLogger {
    private String filename;
    private String title = "LOG";
    public static final String PATH = "/Users/nsimsiri/Documents/code/web/sample-projects/strain-core/fitbit-web-service/log/";
    public FileLogger(String filename){
        this.filename = filename;
    }

    public FileLogger(String filename, String title){
        this.filename = filename;
        this.title = title;
    }
    public void log(String msg){
        String fullPath = PATH + filename + ".txt";
        try {
            File logFile = new File(fullPath);
            logFile.createNewFile();
            String log_hr = "----------------------------------------------------------------";
            String log_block = String.format("%s\n%s %s\n%s\n%s\n", log_hr, title, LocalDateTime.now(), log_hr, msg);

            Files.write(Paths.get(fullPath), log_block.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e){

        }
    }
    public static void main(String[] args){
        FileLogger test = new FileLogger("test", "TEST");
        test.log("Kappa");
    }
}
