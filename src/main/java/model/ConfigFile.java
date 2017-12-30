package model;

import java.util.Map;

public class ConfigFile {

    private Algorythm hashAlgorythm;

    private Map<String, String> filesToValidate;

    private String fileToPatch;

    private String hashBefore;

    private String hashAfter;

    public Algorythm getHashAlgorythm() {
        return hashAlgorythm;
    }

    public void setHashAlgorythm(Algorythm hashAlgorythm) {
        this.hashAlgorythm = hashAlgorythm;
    }

    public Map<String, String> getFilesToValidate() {
        return filesToValidate;
    }

    public void setFilesToValidate(Map<String, String> filesToValidate) {
        this.filesToValidate = filesToValidate;
    }

    public String getFileToPatch() {
        return fileToPatch;
    }

    public void setFileToPatch(String fileToPatch) {
        this.fileToPatch = fileToPatch;
    }

    public String getHashBefore() {
        return hashBefore;
    }

    public void setHashBefore(String hashBefore) {
        this.hashBefore = hashBefore;
    }

    public String getHashAfter() {
        return hashAfter;
    }

    public void setHashAfter(String hashAfter) {
        this.hashAfter = hashAfter;
    }
}
