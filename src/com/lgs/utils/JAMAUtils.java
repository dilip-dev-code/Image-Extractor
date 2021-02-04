
package com.lgs.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

/**
 *
 * @author ShivanshuJS
 */
public class JAMAUtils {
    private final String BASE_URL = "https://iogp.jamacloud.com/rest/latest/files?url=";
    private final String diskLocation = System.getProperty("java.io.tmpdir") + "JAMA" + File.separator + "attachments" + File.separator;
    
    private final String username;
    private final String password;
    private final String resourceURL;

    public JAMAUtils(String username, String password, String resourceURL) {
        this.username = username;
        this.password = password;
        this.resourceURL = resourceURL;
    }
    
    private String getAuthString(){
        String auth = Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes());
        return "Basic " + auth;
    }
    
    private String getFileName(){
        String key = this.resourceURL.substring(this.resourceURL.indexOf("attachment") + "attachment".length() + 1, this.resourceURL.lastIndexOf("/"));
        return key + "_" + this.resourceURL.substring(this.resourceURL.lastIndexOf("/") + 1, this.resourceURL.length());
    }
    
    private void createDirectory(String path){
        File file = new File(path);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
    }
    
    public void extractImageToDisk() throws MalformedURLException, IOException{
        URL url = new URL(this.BASE_URL + this.resourceURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", this.getAuthString());
        InputStream imageStream = httpURLConnection.getInputStream();
        this.createDirectory(this.diskLocation + this.getFileName());
        Files.copy(imageStream, Paths.get(this.diskLocation + this.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }
}
