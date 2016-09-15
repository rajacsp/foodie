package com.byteshaft.foodie.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * class for sending multipart data to server
 */
public class MultiPartUtility {

    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;
    private final HttpURLConnection connection;
    private final OutputStream outputStream;
    private final PrintWriter writer;
    private final String boundary;
    private final URL url;
    private final long start;
    private boolean registrationProcess = false;
    private boolean postProductProcess = false;

    // Method use for registration
    public MultiPartUtility(final URL url, String method) throws IOException {
        registrationProcess = true;
        start  = System.currentTimeMillis() % 1000;
        this.url = url;
        System.out.println(url);
        boundary = "---------------------------" + System.currentTimeMillis() % 1000;
        connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        outputStream = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET),
                true);
    }

    //Method to add form field to printWriter
    public void addFormField(final String name, final String value) {
        writer.append("--").append(boundary).append(CRLF)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(CRLF)
                .append("Content-Type: application/json; charset=").append(CHARSET)
                .append(CRLF).append(CRLF).append(value).append(CRLF);
    }

    //Method to add filePart to printwriter
    public void addFilePart(final String fieldName, final File uploadFile)
            throws IOException {
        final String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(CRLF)
                .append("Content-Disposition: form-data; name=\"")
                .append(fieldName).append("\"; filename=\"").append(fileName)
                .append("\"").append(CRLF).append("Content-Type: ")
                .append("Content-Transfer-Encoding: binary").append(CRLF)
                .append(CRLF);

        writer.flush();
        outputStream.flush();
        FileInputStream inputStream = new FileInputStream(uploadFile);
        final byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();

        writer.append(CRLF);
    }

    // add header to printwriter
    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(CRLF);
    }

    public String finish() throws IOException {
        writer.append(CRLF).append("--").append(boundary).append("--")
                .append(CRLF);
        writer.close();
        final int status = connection.getResponseCode();
        System.out.println(connection.getResponseMessage());
        System.out.println(status);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (connection.getInputStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public byte[] finishFilesUpload() throws IOException {
        writer.append(CRLF).append("--").append(boundary).append("--")
                    .append(CRLF);
        writer.close();
        final int status = connection.getResponseCode();
        System.out.println(status);
        if (registrationProcess && status == 201) {

        }
        if (postProductProcess && status == 201 || status == 200) {

        }
        InputStream is = connection.getInputStream();
        System.out.println(is.toString());
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            bytes.write(buffer, 0, bytesRead);
        }
        return bytes.toByteArray();
    }
}
