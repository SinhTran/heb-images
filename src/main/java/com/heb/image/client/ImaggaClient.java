package com.heb.image.client;

import com.heb.image.exception.ImaggaServerException;
import com.heb.image.model.ImaggaTagResults;
import com.heb.image.util.JsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

@Service
public class ImaggaClient {

    private static final Logger log = Logger.getLogger(ImaggaClient.class.getName());

    private final ImaggaProperties imaggaProperties;

    public ImaggaClient(ImaggaProperties imaggaProperties) {
        this.imaggaProperties = imaggaProperties;
    }

    public ImaggaTagResults getTags(String imageUrl) throws IOException {
        String credentialsToEncode = imaggaProperties.getApiKey() + ":" + imaggaProperties.getApiSecret();
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String url = imaggaProperties.getBasePath() + "/v2/tags?image_url=" + imageUrl;
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", "Basic " + basicAuth);

        int responseCode = connection.getResponseCode();

        log.info("Sending 'GET' request to URL : " + url);
        log.info("Response Code : " + responseCode);
        if (responseCode != 200) {
            throw new ImaggaServerException(responseCode, "Server returned HTTP response code: " + responseCode + " for URL: GET " + url);
        }

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();
        ImaggaTagResults results = JsonMapper.convertFromJsonToObject(jsonResponse, ImaggaTagResults.class);

        return results;
    }

    public ImaggaTagResults getTagsByPOST(MultipartFile imageFile) throws IOException{
        String credentialsToEncode = imaggaProperties.getApiKey() + ":" + imaggaProperties.getApiSecret();
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "Image Upload";

        String url = imaggaProperties.getBasePath() + "/v2/tags";
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imageFile.getName() + "\"" + crlf);
        request.writeBytes(crlf);

        request.write(imageFile.getBytes());

        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
        request.close();

        int responseCode = connection.getResponseCode();

        log.info("Sending 'POST' request to URL : " + url);
        log.info("Response Code : " + responseCode);

        if (responseCode != 200) {
            throw new ImaggaServerException(responseCode, "Server returned HTTP response code: " + responseCode + " for URL: POST " + url);
        }

        InputStream responseStream = new BufferedInputStream(connection.getInputStream());

        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        responseStreamReader.close();

        String response = stringBuilder.toString();

        responseStream.close();
        connection.disconnect();
        ImaggaTagResults results = JsonMapper.convertFromJsonToObject(response, ImaggaTagResults.class);
        return results;
    }
}
