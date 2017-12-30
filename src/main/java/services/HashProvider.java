package services;

import java.nio.file.Path;

public interface HashProvider {

    String getHash(Path path);
}
