import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wuye on 2017/2/18.
 */
public class DataProcess {

    private static final Pattern PATTERN = Pattern.compile("\\[(\\S+)\\s(\\S+)\\]");
    private static final DateFormat FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
    private static final long START = convertToDate("30/Apr/1998:21:30:17").getTime();
    private static final long END = convertToDate("26/Jul/1998:21:59:55").getTime();

    private static final Comparator<Count> ORDERED = Comparator.comparingLong(o -> o.offset);

    private static final Comparator<Count> HITS = Comparator.comparingInt(o -> o.count);

    private static int no = 0;

    public static void process(Path inputPath, Path outputPath, int interval) {
        List<Count> result = readFile(inputPath, interval * 60);
        List<Count> ordered = result.stream().sorted(ORDERED).collect(Collectors.toList());
        List<Count> hits = result.stream().sorted(HITS).collect(Collectors.toList());
        output(outputPath, ordered, hits);
    }

    public static List<Count> readFile(Path path, int interval) {
        ConcurrentHashMap<Long, Integer> result = new ConcurrentHashMap();
        try (Stream<String> stream = Files.lines(path)) {
            System.out.println("read file");
            stream.forEach(line -> processLine(line, result, interval));
            return result.entrySet().stream().map(entry -> new Count(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public static void processLine(String line, ConcurrentHashMap<Long, Integer> map, int interval) {
        System.out.println(++no);
        long offset = parseLine(line);
        if (interval > 0)
            offset = offset / interval;
        Integer count = map.get(offset);
        if (count == null) {
            count = 0;
        }
        map.put(offset, ++count);
    }

    public static long parseLine(String line) {
        Matcher m = PATTERN.matcher(line);
        if (m.find()) {
            Date date = convertToDate(m.group(1));
            return (date.getTime() - START) / 1000;
        }
        return -1;
    }

    private static Date convertToDate(String time) {
        try {
            return FORMAT.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void output(Path outPath, List<Count> ordered, List<Count> hits) {
        writeFile(outPath, ordered);
        Path path = Paths.get(outPath + ".hit");
        writeFile(path, hits);
    }

    private static void writeFile(Path path, List<Count> counts) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Count hit : counts) {
                String line = new StringBuilder("")
                        .append(hit.offset)
                        .append(" ")
                        .append(hit.count).toString();
                writer.append(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Count {
        public long offset;
        public int count;

        public Count(long offset, int count) {
            this.offset = offset;
            this.count = count;
        }
    }

}
