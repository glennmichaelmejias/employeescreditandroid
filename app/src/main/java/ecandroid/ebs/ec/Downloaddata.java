package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Array;

public class Downloaddata extends AppCompatActivity {
    Typeface font;
    Ajax mo,mo2,mo3;
    Globalvars globalvars;
    ProgressBar loadingbar;
    TextView txtviewstatus;
    Msgbox msgbox;
    Context context = this;
    SQLiteDatabase mydatabase;

    int length=0;
    JSONArray thedata;
    private static final String TAG = Downloaddata.class.getSimpleName();
    private Handler mHandler;
    String emp_no,emp_pins,name,aggregate_con,aggregate_reg,companyname,businessunit,deptname,payrollno,emp_id;
    String  itemcode,description,quantity,unit,cost,bcode,expirydate,startdate,enddate,itemid,theimg;
    String ratee,subordinates_rater;
    File folder;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        msgbox = new Msgbox(context);
        globalvars = new Globalvars((Context)this,(Activity)this);
        setContentView(R.layout.activity_downloaddata);
        font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        loadingbar = (ProgressBar) findViewById(R.id.loadingbar);
        txtviewstatus = (TextView) findViewById(R.id.txtviewstatus);
        final Button btncheckconnection = (Button) findViewById(R.id.btncheckconnection);
        final Button btnstartdownload = (Button) findViewById(R.id.btnstartdownload);
        btncheckconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingbar.setVisibility(View.VISIBLE);
                txtviewstatus.setText("Checking server connection. Please wait...");
                checkconnection();
            }
        });

        btnstartdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatedownload();
            }
        });
        setfontawesome(R.id.txtviewdownloaddata,"\uf0ed");
        checkconnection();
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
    }
    public void initiatedownload(){
        txtviewstatus.setText("Downloading employee masterfile from server. Please wait...");
        loadingbar.setVisibility(View.VISIBLE);
        mo = new Ajax();
        loadingbar.setProgress(1);

        mo.setCustomObjectListener(new Ajax.MyCustomObjectListener(){
            @Override
            public void onerror() {
                new android.os.Handler().postDelayed(
                    new Runnable(){
                        public void run(){
                            loadingbar.setVisibility(View.INVISIBLE);
                            txtviewstatus.setText("Unable to connect to server. Please make sure you are connected to a wifi.");
                        }
                    },1000
                );
            }
            @Override
            public void onsuccess(final String data){
                mydatabase.execSQL("DELETE FROM employeemasterfile");
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        loadingbar.setProgress(30);
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            thedata = new JSONArray(data);
                            mydatabase.beginTransaction();
                            for(int a=0;a < thedata.length();a++){
                                ContentValues cv = new ContentValues();
                                JSONArray row = thedata.getJSONArray(a);
                                emp_no = row.getString(0);
                                emp_pins = row.getString(1);
                                name = row.getString(2);
                                aggregate_con = row.getString(3);
                                aggregate_reg = row.getString(4);
                                companyname = row.getString(5);
                                businessunit = row.getString(6);
                                deptname = row.getString(7);
                                payrollno = row.getString(8);
                                emp_id = row.getString(9);
                                cv.put("emp_no",emp_no);
                                cv.put("emp_pins",emp_pins);
                                cv.put("name",name);
                                cv.put("aggregate_con",aggregate_con);
                                cv.put("aggregate_reg",aggregate_reg);
                                cv.put("companyname",companyname);
                                cv.put("businessunit",businessunit);
                                cv.put("deptname",deptname);
                                cv.put("payrollno",payrollno);
                                cv.put("emp_id",emp_id);
                                mydatabase.insert("employeemasterfile",null,cv);
                            }
                            mydatabase.setTransactionSuccessful();
                            mydatabase.endTransaction();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingbar.setProgress(40);
                                    initiatedownload2();
                                }
                            },1000);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },100);
            }
        });
        mo.execute(this.getString(R.string.serveraddress) + "android_downloaddata_employee.php");
    }
    public void initiatedownload2(){
        txtviewstatus.setText("Downloading item masterfile from server. Please wait...");
        mo2 = new Ajax();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadingbar.setProgress(60);
            }
        });
        folder = new File("sdcard/ecimages");
        mo2.setCustomObjectListener(new Ajax.MyCustomObjectListener(){
            @Override
            public void onerror() {

            }
            @Override
            public void onsuccess(final String data){
                mydatabase.execSQL("DELETE FROM ecform");
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        try {
                            thedata = new JSONArray(data);
                            mydatabase.beginTransaction();
                            for(int a=0;a < thedata.length();a++){
                                ContentValues cv = new ContentValues();
                                JSONArray row = thedata.getJSONArray(a);
                                itemcode = row.getString(0);
                                description = row.getString(1);
                                quantity = row.getString(2);
                                unit = row.getString(3);
                                cost = row.getString(4);
                                bcode = row.getString(5);
                                expirydate = row.getString(6);
                                startdate = row.getString(7);
                                enddate = row.getString(8);
                                itemid = row.getString(9);
                                theimg = row.getString(10);
                                byte[] decodedString = Base64.decode(theimg, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                FileOutputStream out = null;
                                try {
                                    out = new FileOutputStream("sdcard/ecimages/"+itemid+".png");
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
                                }
                                decodedByte.compress(Bitmap.CompressFormat.PNG, 100, out);

                                cv.put("id",itemid);
                                cv.put("code",itemcode);
                                cv.put("description",description);
                                cv.put("qty",quantity);
                                cv.put("unit",unit);
                                cv.put("cost",cost);
                                cv.put("expirydate",expirydate);
                                cv.put("startdate",startdate);
                                cv.put("enddate",enddate);

                                mydatabase.insert("ecform",null,cv);

                            }
                            mydatabase.setTransactionSuccessful();
                            mydatabase.endTransaction();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingbar.setProgress(70);
                                    downloadsubordinate();
                                }
                            },1000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },1000);
            }
        });
        mo2.adddata("inchargeempid",globalvars.get("inchargeempid"));
        mo2.execute(this.getString(R.string.serveraddress)+"android_downloaddata_item.php");
    }
    public void downloadsubordinate(){
        txtviewstatus.setText("Downloading employee leveling subordinates from server. Please wait...");
        loadingbar.setVisibility(View.VISIBLE);
        mo3 = new Ajax();
        mo3.setCustomObjectListener(new Ajax.MyCustomObjectListener(){
            @Override
            public void onerror() {
                new android.os.Handler().postDelayed(
                        new Runnable(){
                            public void run(){
                                loadingbar.setVisibility(View.INVISIBLE);
                                txtviewstatus.setText("Unable to connect to server. Please make sure you are connected to a wifi.");
                            }
                        },1000
                );
            }
            @Override
            public void onsuccess(final String data){
                mydatabase.execSQL("DELETE FROM levelingsubordinates");
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        loadingbar.setProgress(90);
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        try {
                            thedata = new JSONArray(data);
                            mydatabase.beginTransaction();
                            for(int a=0;a < thedata.length();a++){
                                ContentValues cv = new ContentValues();
                                JSONArray row = thedata.getJSONArray(a);
                                ratee = row.getString(0);
                                subordinates_rater = row.getString(1);
                                cv.put("ratee",ratee);
                                cv.put("subordinates_rater",subordinates_rater);
                                mydatabase.insert("levelingsubordinates",null,cv);
                            }
                            mydatabase.setTransactionSuccessful();
                            mydatabase.endTransaction();

                            txtviewstatus.setText("Data successfully updated!");
                            loadingbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Data successfully updated!", Toast.LENGTH_SHORT).show();
                            loadingbar.setProgress(0);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },100);
            }
        });
        mo3.execute(this.getString(R.string.serveraddress)+"android_downloaddata_leveling_subordinates.php");
    }
    public void checkconnection(){
        mo = new Ajax();
        mo.setCustomObjectListener(new Ajax.MyCustomObjectListener(){
            @Override
            public void onerror(){
                new android.os.Handler().postDelayed(
                    new Runnable(){
                        public void run(){
                            loadingbar.setVisibility(View.INVISIBLE);
                            txtviewstatus.setText("Unable to connect to server. Please make sure you are connected to a wifi.");
                        }
                    },1000
                );
            }
            @Override
            public void onsuccess(final String data) {
                new android.os.Handler().postDelayed(
                    new Runnable(){
                        @Override
                        public void run(){
                            loadingbar.setVisibility(View.INVISIBLE);
                            txtviewstatus.setText("Connection established. Ready for download.");
                        }
                    },1000
                );
            }
        });
        mo.execute(this.getString(R.string.serveraddress) + "android_checkconnection.php");

    }
    public void setfontawesome(int id,String icon){
        TextView txtview = (TextView) findViewById(id);
        txtview.setTypeface(font);
        txtview.setText(icon);
    }

}
