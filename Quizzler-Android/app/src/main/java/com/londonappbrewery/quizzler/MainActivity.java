package com.londonappbrewery.quizzler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.github.kexanie.library.MathView;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {


    public static String url = "https://api.myjson.com/bins/cbljy";

   // public static String url = "http://localhost:3000/contacts/";

    ArrayList<HashMap<String, String>> contactsList;

    private ProgressDialog pDialog;

    boolean AnsQuiz[];

    int cout = 0;
    int c = 1;
    int NextCount=0;

    TextView editQuiz;
    ProgressBar setProgess;
    int maxProgress;

    private boolean Answer[] =
            {true,true,true,true,true,true,true,true,true,true,true,true,true};


    SeekBar mProgressBar;
    CountDownTimer mCountDownTimer,CountDownStartQuiz;

    int i=0,o=1;
    int selectQuiz,selectAns = 0;

    AlertDialog.Builder alertadd;

    MathView mathQuiz,mathAns;

    PieChartView ChartQuiz,ChartAns;
    List<SliceValue> QuizList,AnsList;
    TextView mTxt,textCount,result1,result2,showCount,showScore;

    ProgressBar pbar;
    Button sendAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsList = new ArrayList<>();

        new GetContacts().execute();

        mathQuiz = (MathView) findViewById(R.id.math1);
        mathAns = (MathView) findViewById(R.id.math2);

        mProgressBar=(SeekBar) findViewById(R.id.seekBar4);
        pbar = (ProgressBar)findViewById(R.id.progress_bar);

        mTxt = (TextView)findViewById(R.id.mText);
        textCount = (TextView)findViewById(R.id.textCount);
        showCount = (TextView) findViewById(R.id.Showcount);
        showScore = (TextView) findViewById(R.id.Showscore);


        result1 = (TextView)findViewById(R.id.result1);
        result2 = (TextView)findViewById(R.id.result2);

        sendAns = (Button)findViewById(R.id.btsendans);

        System.out.println("OS " + contactsList.size());

        //PieChartView
        ChartQuiz = findViewById(R.id.chartQuiz);
        QuizList = new ArrayList<>();

        ChartAns = findViewById(R.id.chartAns);
        AnsList = new ArrayList<>();

        //Select ChartAns

         //Hiddle();

        ChartAns.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                if (AnsList.get(arcIndex).getColor() == Color.parseColor("#ffffff")) {

                    AnsList.set(arcIndex, (new SliceValue(AnsList.get(arcIndex).getValue(), Color.parseColor("#ffcc00"))));

                    selectAns ++;

                    //System.out.println("Color : " + AnsList.get(arcIndex).getColor()+ "==" +Color.parseColor("#ffcc00"));
                }else {
                    AnsList.set(arcIndex, (new SliceValue(AnsList.get(arcIndex).getValue(), Color.parseColor("#ffffff"))));

                    selectAns --;
                }

//                Toast.makeText(MainActivity.this,
//                        "Index : "+arcIndex, Toast.LENGTH_SHORT).show();

                mathAns.setText("$$\\frac{" + selectAns + "}{" + AnsList.size() + "}$$");

                double p = ((double)selectAns/AnsList.size());

                result2.setText(String.format("= %.4f", p));

            }

            @Override
            public void onValueDeselected() {

            }
        });


        sendAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for (int i=0;i<contactsList.size();i++) {
//                    System.out.println("No is "+contactsList.get(i).get("no"));
//                }
               // System.out.println("เฉลย " + contactsList.get(o).get("anwup"));
                System.out.println("ตอบ "+selectAns);

                mCountDownTimer.cancel();
                checkQuiz(selectAns,o-2);
                StartQuiz();

            }
        });

    }

    private void Hiddle() {
        ChartQuiz.setVisibility(View.GONE);
        ChartAns.setVisibility(View.GONE);
        result1.setVisibility(View.GONE);
        result2.setVisibility(View.GONE);
        mathAns.setVisibility(View.GONE);
        mathQuiz.setVisibility(View.GONE);
        mTxt.setVisibility(View.GONE);
    }

    public class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading....");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG,"respone " + jsonStr);

            if (jsonStr != null){
                try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray contacts = jsonObject.getJSONArray("game1");

                for (int i =0;i<contacts.length();i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String no = c.getString("no");
                    String quizup = c.getString("quizup");
                    String quizdown = c.getString("quizdown");
                    String anwup = c.getString("anwup");
                    String anwdown = c.getString("anwdown");
                    String mode = c.getString("mode");


                    HashMap<String, String> contact = new HashMap<>();

                    contact.put("no", no);
                    contact.put("quizup", quizup);
                    contact.put("quizdown", quizdown);
                    contact.put("anwup", anwup);
                    contact.put("anwdown", anwdown);
                    contact.put("mode", mode);

                    contactsList.add(contact);
                  }
                } catch (final JSONException e){

                    Log.e(TAG,"REsponse: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"Json error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }else {

                Log.e(TAG,"Cound get from server: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Cound get from server: ",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing()){
                pDialog.dismiss();
            }

           // System.out.println(Integer.valueOf(contactsList.get(0).get("quizdown")));

            AnsQuiz = new boolean[10];
            Showall();
            StartQuiz();
               // System.out.println(contactsList.get(i).get("no"));

           // System.out.println("ISSS "+contactsList.get(0).get("id") + contactsList.get(1).get("id"));
        }
    }

    private void Showall() {
        ChartQuiz.setVisibility(View.VISIBLE);
        ChartAns.setVisibility(View.VISIBLE);
        result1.setVisibility(View.VISIBLE);
        result2.setVisibility(View.VISIBLE);
        mathAns.setVisibility(View.VISIBLE);
        mathQuiz.setVisibility(View.VISIBLE);
        mTxt.setVisibility(View.VISIBLE);
    }


    private void addQuizData(int all,int part) {
        QuizList.clear();
        float p = 100/all;
        for (int i =0 ;i<all;i++) {

            if (i < part){
                QuizList.add(new SliceValue(p, Color.parseColor("#ffcc00")));
            }else {
                QuizList.add(new SliceValue(p, Color.parseColor("#ffffff")));
            }
            }

        result1.setText(String.format("= %.4f", (double)part/all));
    }

    private void addAnsData(int k) {
        AnsList.clear();
        float p = 100/k;
        for (int i =0 ;i<k;i++) {
            AnsList.add(new SliceValue(p, Color.parseColor("#ffffff")));
        }
    }

    private  void popup(){
        alertadd = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.text, null);
        alertadd.setView(view);
        alertadd.setNeutralButton("Next Quiz", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                startTimeout(15,0);
            }
        });

        alertadd.show();
    }

    private void startTimeout(int start, int stop) {

        maxProgress = (start-stop)*1000;

        mProgressBar.setMax(maxProgress);
        mProgressBar.setProgress(mProgressBar.getMax());

        mCountDownTimer = new CountDownTimer(maxProgress,10) {
            @Override
            public void onTick(long l) {
                int timeRemaining = (int) l;
                mTxt.setText(String.valueOf((l/1000)+1) );

                mProgressBar.setProgress(timeRemaining);

            }

            @Override
            public void onFinish() {
               // mTxt.setText("หมดเวลา");
                mProgressBar.setProgress(maxProgress);
                System.out.println("IOOO "+o);
                StartQuiz();


            }
        };

        mCountDownTimer.start();


    }

    private void StartQuiz(){

        mProgressBar.setProgress(maxProgress);

        Hiddle();
        showCount.setText(String.valueOf(o)+"/"+contactsList.size());

     if (o <= contactsList.size()) {
    CountDownStartQuiz = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long l) {

                textCount.setText("" + ((l / 1000)+1));
                sendAns.setEnabled(false);

        }

        @Override
        public void onFinish() {

            Showall();
            NextQuize(o-1);
            textCount.setText("");
            mTxt.setText("");
            sendAns.setEnabled(true);
            startTimeout(15, 0);
            o++;

        }
    };

    CountDownStartQuiz.start();
}else{
    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
    startActivity(intent);
    finish();
}
    }



    private void NextQuize(int p) {

        int quizdown = Integer.valueOf(contactsList.get(p).get("quizdown"));
        int quizup = Integer.valueOf(contactsList.get(p).get("quizup"));
        int anwdown = Integer.valueOf(contactsList.get(p).get("anwdown"));

        addQuizData(quizdown,quizup);
        final PieChartData pieDataQuiz = new PieChartData(QuizList);
        ChartQuiz.setPieChartData(pieDataQuiz);

        addAnsData(anwdown);
        final PieChartData pieDataAns = new PieChartData(AnsList);
        ChartAns.setPieChartData(pieDataAns);

        selectAns = 0;

        mathQuiz.setText("$$\\frac{" + quizup + "}{" + quizdown + "}$$");
        mathAns.setText("$$\\frac{" + 0 + "}{" + anwdown + "}$$");
    }


    private void checkQuiz (int ans,int no){
      //  System.out.println("IS " + Integer.valueOf(contactsList.get(no).get("anwup")));
        if (Integer.valueOf(contactsList.get(no).get("anwup")) == ans){
            System.out.println("YESSSS");
            AnsQuiz[no] = true;
            updatescore();
        }else {
            System.out.println("NOOOO");
            AnsQuiz[no] = false;
        }
    }

    private void updatescore(){
         pbar.setProgress(1);
    }


}
