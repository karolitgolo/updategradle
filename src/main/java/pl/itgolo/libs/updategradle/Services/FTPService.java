package pl.itgolo.libs.updategradle.Services;

import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The type Ftp service.
 */
public class FTPService {

    /**
     * The Ftp client.
     */
    FTPClient ftpClient;
    /**
     * The Ftp server.
     */
    String ftpServer;
    /**
     * The Ftp port.
     */
    Integer ftpPort;
    /**
     * The Ftp user.
     */
    String ftpUser;
    /**
     * The Ftp password.
     */
    String ftpPassword;
    /**
     * The Local passive mode.
     */
    Boolean localPassiveMode;
    /**
     * The File type.
     */
    Integer fileType;

    /**
     * Instantiates a new Ftp service.
     *
     * @param ftpServer        the ftp server
     * @param ftpPort          the ftp port
     * @param ftpUser          the ftp user
     * @param ftpPassword      the ftp password
     * @param localPassiveMode the local passive mode
     * @param fileType         the file type
     * @throws IOException the io exception
     */
    public FTPService(String ftpServer, Integer ftpPort, String ftpUser, String ftpPassword, Boolean localPassiveMode, Integer fileType) throws IOException {
        this.ftpServer = ftpServer;
        this.ftpPort = ftpPort;
        this.ftpUser = ftpUser;
        this.ftpPassword = ftpPassword;
        this.localPassiveMode = localPassiveMode;
        this.fileType = fileType;
        initFTPClient();
    }

    /**
     * Close ftp client.
     *
     * @throws IOException the io exception
     */
    public void closeFtpClient() throws IOException {
        ftpClient.logout();
        ftpClient.disconnect();
    }


    /**
     * Init ftp client.
     *
     * @throws IOException the io exception
     */
    public void initFTPClient() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ftpServer, ftpPort);
        if (!ftpClient.login(ftpUser, ftpPassword)) {
            throw new IOException("Can not login to FTP");
        }
        if (localPassiveMode) {
            ftpClient.enterLocalPassiveMode();
        }
        ftpClient.setFileType(fileType);
    }


    /**
     * Make directories.
     *
     * @param file       the file
     * @param remotePath the remote path
     * @throws IOException the io exception
     */
    public void makeDirectories(File file, String remotePath) throws IOException {
        String remoteDir = getCorrectRemoteDir(remotePath, file);
        ftpClient.changeWorkingDirectory("/");
        boolean dirExists = true;
        String[] directories = remoteDir.split("/");
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (dirExists) {
                    dirExists = ftpClient.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    if (!ftpClient.makeDirectory(dir)) {
                        throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + ftpClient.getReplyString() + "'");
                    }
                    if (!ftpClient.changeWorkingDirectory(dir)) {
                        throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + ftpClient.getReplyString() + "'");
                    }
                }
            }
        }
        if (file.isDirectory()){
            System.out.println("Uploaded directory to FTP: " + remoteDir);
        }
    }

    /**
     * Upload.
     *
     * @param file       the file
     * @param remotePath the remote path
     * @throws IOException the io exception
     */
    public void upload(File file, String remotePath) throws IOException {
        makeDirectories(file, remotePath);
        if (file.isFile()) {
            ftpClient.changeWorkingDirectory(getCorrectRemoteDir(remotePath, file));
            InputStream inputStream = new FileInputStream(file);
            String fileName = Paths.get(remotePath).getFileName().toString();
            OutputStream outputStream = this.ftpClient.storeFileStream(fileName);
            if (Objects.isNull(outputStream)) {
                throw new IOException(ftpClient.getReplyString());
            }
            Integer sizeBytes = 4096;
            byte[] bytesIn = new byte[sizeBytes];
            int read = 0;
            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();
            boolean completed = this.ftpClient.completePendingCommand();
            if (completed) {
                System.out.println("Uploaded file to FTP: " + remotePath);
            } else {
                throw new IOException("Can not upload file to FTP: " + remotePath);
            }
        }
    }


    private String getCorrectRemoteDir(String remotePath, File file) {
        if (file.isFile()) {
            remotePath = Paths.get(remotePath).getParent().toString();
        }
        remotePath = remotePath.replaceAll("\\\\", "/");
        return remotePath;
    }

}