package com.example.sandy.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationofCorrectAnswer))){

            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }else {

            Toast.makeText(getApplicationContext(),"Worng! It Was " +celebNames.get(chosenCeleb),Toast.LENGTH_LONG).show();
        }

        createNewQuestion();
    }

    ArrayList<String> celebURL=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();

    int chosenceleb=0;
    int locationofCorrectAnswer=0;
    String[] answer=new String[4];

    int chosenCeleb=0;
    ImageView imageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url=new URL(urls[0]);

                HttpURLConnection connection=(HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream=connection.getInputStream();

                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {



            String results="";
            URL url;
            HttpURLConnection urlConnection=null;

            try{

                url=new URL(urls[0]);

                urlConnection=(HttpURLConnection) url.openConnection();

                InputStream in=urlConnection.getInputStream();

                InputStreamReader reader=new InputStreamReader(in);

                int data=reader.read();

                while (data!=-1)
                {
                    char current=(char)data;

                    results +=current;

                    data=reader.read();
                }

                return results;


            }catch (Exception e){

                e.printStackTrace();

            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView) findViewById(R.id.imageView);

        button0=(Button)findViewById(R.id.button);
        button1=(Button)findViewById(R.id.button2);
        button2=(Button)findViewById(R.id.button3);
        button3=(Button)findViewById(R.id.button4);

        DownloadTask task=new DownloadTask();
        String result=null;

        try {
            result=task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult=result.split("<div class=\"sidebarInnerContainer\">");

            Pattern p=Pattern.compile("img src=\"(.*?)\"");

            Matcher m=p.matcher(splitResult[0]);

            while (m.find()){

                celebURL.add(m.group(1));
            }

           p=Pattern.compile("alt=\"(.*?)\"");

           m=p.matcher(splitResult[0]);

            while (m.find()){

                celebNames.add(m.group(1));
            }



        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();
        }

        createNewQuestion();

    }

    public void  createNewQuestion(){

        Random random=new Random();
        chosenCeleb=random.nextInt(celebURL.size());

        ImageDownloader imageTask=new ImageDownloader();

        Bitmap celebImage;

        try {
            celebImage = imageTask.execute(celebURL.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            locationofCorrectAnswer=random.nextInt(4);
            int incorrectAnswerlocation;

            for(int i=0;i<4;i++)
            {
                if(i==locationofCorrectAnswer){

                    answer[i]=celebNames.get(chosenCeleb);

                }else{

                    incorrectAnswerlocation=random.nextInt(celebURL.size());

                    while (incorrectAnswerlocation==chosenCeleb){

                        incorrectAnswerlocation=random.nextInt(celebURL.size());
                    }

                    answer[i]=celebNames.get(incorrectAnswerlocation);
                }

            }

            button0.setText(answer[0]);
            button1.setText(answer[1]);
            button2.setText(answer[2]);
            button3.setText(answer[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
