import com.google.gson.Gson;
import exceptions.CantReadConfigFileException;
import exceptions.NoDiffFilesException;
import exceptions.NotAZipFileException;
import exceptions.WrongDiffContentsException;
import model.ConfigFile;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import services.HashProvider;
import services.HashProviderCrc32;
import services.HashProviderSHA256;
import services.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Diff {

    private final static String BACKUP_DIR = "BinaryDiffBackups";

    private Path tmpPath;
    private Path diffFilePath;
    private Gson gson = new Gson();
    private ConfigFile configFile;
    private HashProvider provider = new HashProviderSHA256();


    public static void main(String... args) {
        try {
            FileOutputStream file = new FileOutputStream("BinaryDiffLog.txt", true);
            Logger tee = new Logger(file, System.out);
            System.setOut(tee);
            System.setErr(tee);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't create log file!");
        }

        Diff diff = new Diff();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Starting execution on " + dtf.format(now));
        System.out.println("Working in: " + Paths.get("").toAbsolutePath().toString());
        try{
            diff.doThePatches();
            System.out.println("Execution ended successfully");
            System.out.println("=======================================================");
        } catch(Exception e){
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Information for the programmers below:");
            e.printStackTrace();
        }

    }

    private void doThePatches() {
        try {
            extractPatcher();

            List<Path> list = Files.list(Paths.get(""))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".diff"))
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                throw new NoDiffFilesException("There are no .diff files in the current directory!");
            }
            for (Path p : list) {
                System.out.println("Handling file: " + p.toString());
                tmpPath = Files.createTempDirectory(null);
                List<Path> filesInDiff = extractDiffFile(p);
                readDiffFileExtractedContent(p, filesInDiff);
                initHashProvider();
                if (!checkPrerequisitesFromConfigFile(p)) {
                    cleanUpTmp();
                    tmpPath.toFile().delete();
                    continue;
                }
                doThePatch();
                cleanUpTmp();
                tmpPath.toFile().delete();
            }

        } catch (IOException | ZipException e) {
            System.out.println("Problems with file access or something");
            e.printStackTrace();
        } finally {
            removePatcher();
        }
    }

    private void extractPatcher() throws IOException {
        URL inputUrl = getClass().getResource("bspatch.exe");
        File dest = new File("bspatch.exe");
        if (dest.exists()) {
            boolean deleted = dest.delete();
            if (!deleted)
                throw new IOException("Couldn't delete old bspatch.exe");
        }
        FileUtils.copyURLToFile(inputUrl, dest);
    }

    private void readDiffFileExtractedContent(Path zipPath, List<Path> filesInDiff) {

        Path config = filesInDiff.get(0).toString().endsWith(".json") ? filesInDiff.get(0) : filesInDiff.get(1);
        diffFilePath = filesInDiff.get(0).toString().endsWith(".json") ? filesInDiff.get(1) : filesInDiff.get(0);
        try {
            configFile = gson.fromJson(new FileReader(config.toFile()), ConfigFile.class);
        } catch (FileNotFoundException e) {
            throw new CantReadConfigFileException("Can't read config file: " + config.toString() + " in .diff file " + zipPath.toString());
        }


    }

    private void initHashProvider() {
        switch (configFile.getHashAlgorythm()) {
            case CRC32:
                provider = new HashProviderCrc32();
                break;
            case SHA256:
                provider = new HashProviderSHA256();
                break;
        }
    }

    private List<Path> extractDiffFile(Path file) throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(file.toAbsolutePath().toString());
        if (!zipFile.isValidZipFile()) {
            throw new NotAZipFileException("File " + file.toString() + " is not a proper zip file");
        }
        zipFile.extractAll(tmpPath.toAbsolutePath().toString());
        List<Path> list = Files.list(tmpPath).collect(Collectors.toList());
        if (list.size() != 2) {
            throw new WrongDiffContentsException(file.toString() + " .diff file has wrong amount of files inside");
        }
        return list;
    }

    private boolean checkPrerequisitesFromConfigFile(Path zipFile) {
        Path fileToPath = Paths.get(configFile.getFileToPatch());
        if (!fileToPath.toFile().exists()) {
            System.out.println("File " + configFile.getFileToPatch() + " doesn't exist, skipping " + zipFile.toString());
            return false;
        }
        if (!provider.getHash(fileToPath).equalsIgnoreCase(configFile.getHashBefore())) {
            System.out.println("File " + configFile.getFileToPatch() + " has different hash than expected, skipping " + zipFile.toString());
            return false;
        }
        for (Map.Entry<String, String> entry : configFile.getFilesToValidate().entrySet()) {
            Path entryFileToPath = Paths.get(entry.getKey());
            if (!entryFileToPath.toFile().exists()) {
                System.out.println("File " + entry.getKey() + " doesn't exist, skipping " + zipFile.toString());
                return false;
            }
            if (!provider.getHash(entryFileToPath).equalsIgnoreCase(entry.getValue())) {
                System.out.println("File " + configFile.getFileToPatch() + " has different hash than expected, skipping " + zipFile.toString());
                return false;
            }
        }
        return true;
    }

    private void doThePatch() throws IOException {
        Path fileToPatch = Paths.get(configFile.getFileToPatch());
        if(!Paths.get(BACKUP_DIR).toFile().exists())
        {
            Files.createDirectory(Paths.get(BACKUP_DIR));
        }
        Path backup = Paths.get(BACKUP_DIR + File.separator + configFile.getFileToPatch() + ".back");
        Files.move(fileToPatch, backup, StandardCopyOption.REPLACE_EXISTING);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("cmd", "/c", "bspatch.exe", backup.toString(), fileToPatch.toString(), diffFilePath.toString());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (!provider.getHash(fileToPatch).equalsIgnoreCase(configFile.getHashAfter())) {
            //rollback
            Files.move(backup, fileToPatch, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Change didn't result in expected hash, rolled back changes to " + configFile.getFileToPatch());
        } else {
            System.out.println("Successfully patched " + configFile.getFileToPatch());
        }
        cleanUpTmp();
    }

    private void cleanUpTmp() throws IOException {
        Files.list(tmpPath).forEach(f -> f.toFile().delete());
    }

    private void removePatcher() {
        Paths.get("bspatch.exe").toFile().delete();
    }
}
