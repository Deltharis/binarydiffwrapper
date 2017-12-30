package services;

import exceptions.CantCreateHashException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.CRC32;

public class HashProviderCrc32 implements HashProvider {

    @Override
    public String getHash(Path path) {
        try {
            CRC32 crc = new CRC32();
            crc.update(IOUtils.toByteArray(path.toUri()));
            return String.valueOf(crc.getValue());
        } catch (IOException e) {
            throw new CantCreateHashException("Can't create hash for file " + path);
        }
    }
}
