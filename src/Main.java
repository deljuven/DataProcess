import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length > 1) {
            String inPath = args[0], outPath = args[1];
            int interval = -1;
            if (args.length > 2)
                interval = Integer.valueOf(args[2]);
            long begin = System.currentTimeMillis();
            if (Files.exists(Paths.get(inPath)))
                DataProcess.process(inPath, outPath, interval);
            System.out.println(System.currentTimeMillis() - begin);
        } else
            System.out.println("No input file defined");
    }
}
