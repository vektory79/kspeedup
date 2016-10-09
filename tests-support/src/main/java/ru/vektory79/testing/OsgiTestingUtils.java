package ru.vektory79.testing;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by vektor on 17.05.15.
 */
public class OsgiTestingUtils {
    public static JavaArchive createOsgiBundle() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "mybundle.jar");
        final ResourceAdapterArchive archiveAdapter = archive.as(ResourceAdapterArchive.class);
        Path buildDir = Paths.get("target/classes");

        try (Stream<Path> stream = Files.walk(buildDir, FileVisitOption.FOLLOW_LINKS)) {
            stream.forEach((path) -> {
                if (Files.exists(path) && !Files.isDirectory(path)) {
                    Path p = buildDir.getParent().relativize(path);
                    p = p.subpath(1, p.getNameCount());
                    archiveAdapter.addAsLibrary(path.toFile(), p.toString().replace('\\', '/'));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        archive.writeTo(System.out, Formatters.VERBOSE);
        return archive.as(JavaArchive.class);
    }
}
