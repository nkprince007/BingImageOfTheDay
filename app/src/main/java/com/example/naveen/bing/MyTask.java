package com.example.naveen.bing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MyTask extends AsyncTask<Void, Integer, Boolean> {

    static String imagePath;
    Activity activity;
    String name;
    String results;
    ProgressDialog progressDialog;
    String resolution = "1920x1080";
    String url = "http://www.bing.com/HPImageArchive.aspx?format=xml&idx=0&n=1&mkt=en-US";
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public MyTask(Activity activity) {
        this.activity = activity;
        settings = activity.getSharedPreferences("imagePath", 0);
    }

    @Override
    protected void onPreExecute() {
        if (activity != null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading......");
            progressDialog.show();
            imagePath = settings.getString("path", "");
        }
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        results = processXML(url);
        if (!results.startsWith("http://www.bing.com/"))
            results = "http://www.bing.com/" + results + "_" + resolution + ".jpg";
        return downloadImage(results);
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            if (MainActivity.activity != null) {
                MainActivity.imageView.setImageDrawable(Drawable.createFromPath(MyTask.imagePath));
                Toast.makeText(activity, "Downloaded: " + name, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        } else {
            Toast.makeText(activity, "Download failed", Toast.LENGTH_LONG).show();
        }
        editor = settings.edit();
        editor.putString("path", imagePath);
        Log.d("nav", "put" + imagePath);
        editor.apply();
        super.onPostExecute(aBoolean);
    }

    public boolean downloadImage(String url) {
        boolean successful=true;
        URL downloadUrl;
        HttpURLConnection connection = null;
        InputStream is = null;
        FileOutputStream fileOutputStream = null;
        File file;
        try {
            downloadUrl = new URL(url);
            connection = (HttpURLConnection) downloadUrl.openConnection();
            is = connection.getInputStream();
            name = Uri.parse(url).getLastPathSegment();
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/"
                    + name);
            imagePath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/"
                    + name;
            fileOutputStream = new FileOutputStream(file);
            int read;
            byte[] buffer = new byte[1024];
            while ((read = is.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            successful = true;
        } catch (Exception e) {
            //successful = false;
            //Log.d("nav", e.getMessage());
        }
        Log.d("nav", "got" + imagePath);
        return successful;
    }

    public String processXML(String url) {
        String urlBase = "";
        try {
            // Log.d("nav", "XML processing began.....");
            URL downloadURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();
            connection.setRequestMethod("GET");
            //   Log.d("nav", "Connection established.....");
            InputStream is = connection.getInputStream();
            //  Log.d("nav", "InputStream obtained.....");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = db.parse(is);
            // Log.d("nav", "XML doc obtained.....");
            Element rootElement = xmlDocument.getDocumentElement();
            //  Log.d("nav", "Root element obtained.....");
            NodeList itemsList = rootElement.getElementsByTagName("image");
            //  Log.d("nav", "<image> element list obtained.....");
            Node currentItem, currentChild;
            NodeList itemChild;
            for (int i = 0; i < itemsList.getLength(); i++) {
                currentItem = itemsList.item(i);
                itemChild = currentItem.getChildNodes();
                for (int j = 0; j < itemChild.getLength(); j++) {
                    currentChild = itemChild.item(j);
                    if (currentChild.getNodeName().equalsIgnoreCase("urlBase")) {
                        urlBase = (currentChild.getTextContent());
                    }
                }
            }
            // Log.d("nav", "XML processing finished.....");
        } catch (SAXException | MalformedURLException | ParserConfigurationException | ProtocolException e) {
            //  Log.d("nav", "XML processing failed.....");
        } catch (IOException e) {
            // Log.d("nav", "XML processing failed.....");
        }
        return urlBase;
    }

}
