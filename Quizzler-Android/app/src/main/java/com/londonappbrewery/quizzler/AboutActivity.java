package com.londonappbrewery.quizzler;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class AboutActivity extends AppCompatActivity {

    String value;
    int quizlist,truequiz;
    PieChartView chart;
    List<SliceValue> chartlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        System.out.println("OK TEstt");

        TextView show = (TextView) findViewById(R.id.text007);
        chart = (PieChartView) findViewById(R.id.chart2) ;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
            quizlist = extras.getInt("quizlist");
            truequiz = extras.getInt("quiztrue");
            //The key argument here must match that used in the other activity
        }

        PieChartData chartdata1 = new PieChartData();
        chartlist = new ArrayList<>();


        addQuizData(quizlist,truequiz);
        final PieChartData pieDataQuiz = new PieChartData(chartlist);
        chart.setPieChartData(pieDataQuiz);

        System.out.println("= === "+value+quizlist+truequiz +"asdad ");
        show.setText("ถูก " +truequiz+ " ข้อ จาก"+quizlist +"ข้อ" );

        chart.setVisibility(View.GONE);
    }

    private void addQuizData(int all,int part) {
        chartlist.clear();
        float p = 100/all;
        for (int i =0 ;i<all;i++) {

            if (i < 5){
                chartlist.add(new SliceValue(p, Color.parseColor("#1DE447")));
            }else {
                chartlist.add(new SliceValue(p, Color.parseColor("#DB3434")));
            }
        }

    }
}
