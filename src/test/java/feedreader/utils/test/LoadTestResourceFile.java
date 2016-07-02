package feedreader.utils.test;

import feedreader.test.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoadTestResourceFile {

    public static File asFile(String fileName) {
        String pwd = System.getProperty("user.dir");
        Path p = Paths.get(pwd + Config.TEST_SRC + fileName);
        return p.toFile();
    }

    // TODO: make src/test/resources available via ant as system.property.
    public static FileInputStream asFileStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(asFile(fileName));
    }

}
