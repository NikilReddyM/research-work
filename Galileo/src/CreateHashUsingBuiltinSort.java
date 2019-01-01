import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateHashUsingBuiltinSort
{
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //return complete hash
        return sb.toString();
    }

    private static String[] addToStringArray(String[] originalArray, String newItem)
    {
        int currentSize = originalArray.length;
        int newSize = currentSize + 1;
        String[] tempArray = new String[ newSize ];
        for (int i=0; i < currentSize; i++)
        {
            tempArray[i] = originalArray [i];
        }
        tempArray[newSize- 1] = newItem;
        return tempArray;
    }

    private static String[] listFilesForFolder(final File folder)
    {
        String[] fileNames = {};
        String[] hashes;
        for (final File fileEntry : folder.listFiles())
        {
            if (fileEntry.isDirectory())
            {
                listFilesForFolder(fileEntry);
            }
            else
            {
                fileNames = addToStringArray(fileNames,folder+"/"+fileEntry.getName());
            }
        }
        return fileNames;
    }

    private static String[] getHashes(String[] fileNames)
    {
        String[] hashes = {};

        for(int i=0;i<fileNames.length;i++)
        {
            File file = new File(fileNames[i]);

            try
            {
                //Use MD5 algorithm
                MessageDigest md5Digest = MessageDigest.getInstance("SHA-1");

                //Get the checksum
                String checksum = getFileChecksum(md5Digest, file);

                //see checksum
                hashes = addToStringArray(hashes,checksum);
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return hashes;
    }



    public static void main(String args[])
    {
        long startTime = System.nanoTime();

        final File folder = new File("/Users/nikilreddymamilla/Desktop/galileo/data");
        String[] fileNames = listFilesForFolder(folder);

        String[] hashes = getHashes(fileNames);
        Arrays.sort(hashes);
        for(int i=0;i<hashes.length;i++)
        {
            System.out.println(hashes[i]);
        }

        List<String> tempTxList = new ArrayList<String>();

        for(int i=0;i<hashes.length;i++)
        {
            tempTxList.add(hashes[i]);
        }

        MerkleTrees merkleTrees = new MerkleTrees(tempTxList);
        merkleTrees.merkle_tree();
        System.out.println("root : " + merkleTrees.getRoot());

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Elapsed time in seconds: " + totalTime / 1000000000);
    }
}
