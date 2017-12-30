package services;

import exceptions.CantCreateHashException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashProviderSHA256 implements HashProvider {

     public static void main(String... args){
         System.out.println(new HashProviderSHA256().getHash(Paths.get("G:\\SteamLibrary\\steamapps\\common\\Skyrim Special Edition\\Data\\Dragonborn.esm")));
     }

    @Override
    public String getHash(Path path) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(IOUtils.toByteArray(path.toUri()));
            byte[] digest = messageDigest.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(16);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new CantCreateHashException("Can't create hash for file " + path);
        }
    }
}
