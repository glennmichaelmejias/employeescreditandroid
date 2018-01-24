package ecandroid.ebs.ec;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class ScanEmployee3Details extends AppCompatActivity {
    EditText txtenteremployeeid;
    SQLiteDatabase mydatabase;
    Boolean newid = true;
    String temptext="";
    String employeeid = "";
    Msgbox msgbox;
    Context context=this;
    ImageView thesignature;
    Typeface font;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_employee3_details);
        txtenteremployeeid = (EditText) findViewById(R.id.txtenteremployeeid);
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        msgbox = new Msgbox(context);
        Button btndone = (Button) findViewById(R.id.btnscanemployee3detailsdone);
        Button btncancel = (Button) findViewById(R.id.btnscanemployee3detailscancel);
        font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        btndone.setFocusable(false);
        btncancel.setFocusable(false);

        txtenteremployeeid.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}
            @Override
            public void afterTextChanged(Editable s){
                if(newid == true){
                    temptext = txtenteremployeeid.getText().toString();
                }
            }
        });
        //txtenteremployeeid.setText("201803328935");
        txtenteremployeeid.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if(keyCode == 66 && event.getAction()==0){
                    temptext = txtenteremployeeid.getText().toString();
                    employeeid = temptext;
                    newid = false;
                    txtenteremployeeid.setText("");
                    newid = true;
                    displayemployeeinfo();
                    txtenteremployeeid.requestFocus();
                }
                return false;
            }
        });
        txtenteremployeeid.setInputType(InputType.TYPE_NULL);
        btndone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Cursor resultSet = mydatabase.rawQuery("Select max(requestgroup)+1 from employeeitems",null);
                int requestgroup=0;
                if(resultSet != null) {
                    resultSet.moveToNext();
                    requestgroup = resultSet.getInt(0);
                }
                mydatabase.execSQL("UPDATE employeeitems set emp_id="+qttext(emp_id)+",requestgroup="+qttext(requestgroup)+",requestdate=strftime('%Y-%m-%d %H:%M:%S','now') where emp_id="+qttext("walapa"));
                Toast.makeText(getApplicationContext(),"Saved!",Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(ScanEmployee3Details.this,ScanEmployee3.class));
            }
        });
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
        thesignature = (ImageView) findViewById(R.id.imgviewscanemployee3detailssignature);
        thesignature.post(new Runnable() {
            @Override
            public void run() {
                mBitmap = Bitmap.createBitmap(thesignature.getWidth(), thesignature.getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(mBitmap);


                drawsignature();
            }
        });
        thesignature.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mPath.moveTo(event.getX(), event.getY());
                        mPath.lineTo(event.getX() + 1, event.getY() + 1);
                        canvas.drawPath(mPath, mPaint);
                       // thesignature.setImageBitmap(mBitmap);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPath.lineTo(event.getX(), event.getY());
                        canvas.drawPath(mPath, mPaint);
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                thesignature.setImageBitmap(mBitmap);
                handler.postDelayed(this, 70);
            }
        };
        handler.postDelayed(runnable, 70);

        setfontawesome(R.id.txtviewscanemployee3detailsclearsignature,"\uf014");
        TextView btnclearsignature = (TextView) findViewById(R.id.txtviewscanemployee3detailsclearsignature);
        btnclearsignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.drawColor(Color.WHITE);
                mBitmap.eraseColor(Color.WHITE);
                mPath = new Path();
                drawsignature();
            }
        });
    }
    public void drawsignature(){
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.GRAY);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(30f);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawText("S i g n a t u r e",thesignature.getWidth()/2,(thesignature.getHeight()/2)+70,mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        thesignature.setImageBitmap(mBitmap);
    }
    private Handler handler = new Handler();
    private Paint mPaint;
    Canvas canvas;
    private Path mPath;
    private Bitmap mBitmap;
    private String emp_id;
    public void displayemployeeinfo(){
        if(employeeid!=""){
            Cursor resultSet = mydatabase.rawQuery("Select name,companyname,businessunit,deptname,payrollno,emp_id from employeemasterfile where emp_pins="+employeeid,null);
            resultSet.moveToFirst();
            if(resultSet != null){
                String employeename = resultSet.getString(0);
                String department = resultSet.getString(1)+" / "+resultSet.getString(2)+" / "+resultSet.getString(3);
                String payrollnumber = resultSet.getString(4);
                emp_id = resultSet.getString(5);

                TextView txtviewemployeename = (TextView) findViewById(R.id.txtviewemployeename);
                txtviewemployeename.setText(employeename);
                TextView txtviewdepartment = (TextView) findViewById(R.id.txtviewdepartment);
                txtviewdepartment.setText(department);
                TextView txtviewpayrollnumber = (TextView) findViewById(R.id.txtviewpayrollnumber);
                txtviewpayrollnumber.setText(payrollnumber);
            }
        }
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
    public void setfontawesome(int id,String icon){
        TextView txtview = (TextView) findViewById(id);
        txtview.setTypeface(font);
        txtview.setText(icon);
    }

}
