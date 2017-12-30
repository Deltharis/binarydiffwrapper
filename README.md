# binarydiffwrapper
Tool for applying binary patches

Basic idea for this tool was to enable automation of esm cleaning in Skyrim. Windows only currently.

## How to get
Build using `mvn clean build` in the main directory or download the ready .jar file from releases.

## How to use
There are two possibilities - either you already have .diff files from somewhere or need to create them on your own first.
In the project in "patches" folder I've provided a few.

### If you already have the .diff file

Place the .jar in the same directory as the .diff files and files to be patched. Double click it or type `java -jar binarydiffwrapper-1.0.jar` in console.

The program will go through every .diff file in the current directory, applying them one by one (unless error occurs, in which case it stops).

Output will be shown in console and saved in `BinaryDiffLog.txt` file.

### If you want to create .diff file first

You will need to use bsdiff.exe utility that's bundled in resources folder (or download it straight from source - https://www.pokorra.de/coding/bsdiff.html). 
It's usage is as follows:

`bsdiff.exe oldfile newfile patchfile`

Where `patchfile` is the file you will include in the .diff. .diff itself is a zip file containing exactly two files - patchfile from bsdiff, and configuration json file with .json suffix.

In the resources folder there is an attached sample json file. It's contents are:
- hashAlgorythm - algorythm used for hashes in the config file. Currently CRC32 and SHA256 values are supported.
- filesToValidate - map of additional files that need to be validated, with file name as key and hash as value. Example usage would be to validate
that master .esm is the same one against which you were cleaning
- fileToPatch - filename of file to be patched with the patchFile
- hashBefore - hash of fileToPatch before the operation (if it doesn't match patching won't occur)
- hashAfter - hash of fileToPatch after patching. If it is somehow different than expected (patching mysteriously failed) operation is rolled back.

