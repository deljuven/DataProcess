import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length > 1) {
            String inPath = args[0], outPath = args[1];
            int interval = -1;
            if (args.length > 2)
                interval = Integer.valueOf(args[2]);
            boolean readMode = false;
            if (args.length > 3)
                readMode = Boolean.valueOf(args[3]);
            long begin = System.currentTimeMillis();
            Path path = Paths.get(inPath);
            if (Files.exists(path)) {
                if (Files.isSymbolicLink(path))
                    try {
                        path = Files.readSymbolicLink(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                DataProcess.process(path, Paths.get(outPath), interval, readMode);
            } else {
                System.out.println("File not existed");
            }
            System.out.println(System.currentTimeMillis() - begin);
        } else
            System.out.println("No input file defined");
    }
}
