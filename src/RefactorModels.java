import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class RefactorModels {
    public static void main(String[] args) throws Exception {
        Path dir = Paths.get("ocsms/model");
        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(p -> {
                try {
                    String content = new String(Files.readAllBytes(p));
                    if (!content.contains("implements Serializable")) {
                        content = content.replaceFirst("public class (\\w+)", "import java.io.Serializable;\npublic class $1 implements Serializable");
                        Files.write(p, content.getBytes());
                        System.out.println("Updated " + p);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
