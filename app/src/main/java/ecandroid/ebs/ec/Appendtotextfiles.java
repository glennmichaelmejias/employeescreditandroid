package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Appendtotextfiles extends AppCompatActivity {
    Typeface font;
    Ajax mo;
    ProgressBar loadingbar;
    TextView txtviewstatus;
    Msgbox msgbox;
    Context context = this;
    SQLiteDatabase mydatabase;
    Globalvars globalvars;
    EditText txtinchargeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appendtotextfiles);

        globalvars = new Globalvars((Context)this,(Activity)this);

        msgbox = new Msgbox(context);

        font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        loadingbar = (ProgressBar) findViewById(R.id.loadingbar);
        txtviewstatus = (TextView) findViewById(R.id.txtviewstatus);
        final Button btncheckconnection = (Button) findViewById(R.id.btncheckconnection);
        final Button btnstartappend = (Button) findViewById(R.id.btnstartppend);
        btncheckconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingbar.setVisibility(View.VISIBLE);
                txtviewstatus.setText("Checking server connection. Please wait...");
                checkconnection();
            }
        });

        btnstartappend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdummy();
            }
        });
        setfontawesome(R.id.txtviewdownloaddata,"\uf0ee");
        checkconnection();
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
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
                                txtviewstatus.setText("Unable to connect to ebm server. Please make sure you are connected to a wifi.");
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
                                txtviewstatus.setText("Connection established. Ready to append.");
                            }
                        },1000
                );
            }
        });
        mo.execute(this.getString(R.string.serveraddress) + "android_checkconnection.php");
    }
    public void getdummy(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please scan your Incharge ID");
        txtinchargeid = new EditText(this);
        txtinchargeid.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(txtinchargeid);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                //msgbox.show("asdf",input.getText().toString().substring(0,2));
                if(txtinchargeid.getText().toString().length() == 11 && txtinchargeid.getText().toString().substring(0,2).equalsIgnoreCase("09")){
                    initiateappend();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid Incharge ID",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void initiateappend(){
        Ajax ajax = new Ajax();
        ajax.setCustomObjectListener(new Ajax.MyCustomObjectListener(){
            @Override
            public void onerror(){

            }
            @Override
            public void onsuccess(String data){
                Toast toast = Toast.makeText(getApplicationContext(),"Data successfully uploaded",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
                toast.show();
            }
        });

        Cursor row = mydatabase.rawQuery("select a.emp_id,a.itemid,a.quantity,a.requestgroup,a.approvedby,a.requestdate, (b.cost * quantity) amount " +
                                            "from employeeitems a,ecform b where a.itemid = b.id",null);
        JSONArray arr = new JSONArray();

        Cursor row2 = mydatabase.rawQuery("SELECT sum(qty*cost) FROM ecform",null);
        row2.moveToFirst();
        String totaltocredit = row2.getString(0);
        while(row.moveToNext()){
            String employeeid,itemid,quantity,inchargeempid,requestgroup,approvedby,requestdate,amount;
            JSONObject thejs = new JSONObject();
            employeeid = row.getString(0);
            itemid = row.getString(1);
            quantity = row.getString(2);
            inchargeempid=globalvars.get("inchargeempid");
            requestgroup=row.getString(3);
            approvedby=row.getString(4);
            requestdate=row.getString(5);
            amount=row.getString(6);
            try{
                thejs.put("emp_id",employeeid);
                thejs.put("itemid",itemid);
                thejs.put("quantity",quantity);
                thejs.put("inchargeempid",inchargeempid);
                thejs.put("requestgroup",requestgroup);
                thejs.put("approvedby",approvedby);
                thejs.put("requestdate",requestdate);
                thejs.put("amount",amount);
                thejs.put("inchargeid",txtinchargeid.getText().toString());
                thejs.put("totaltocredit",totaltocredit);
                arr.put(thejs);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ajax.adddata("credits",arr.toString());
        ajax.execute(this.getString(R.string.serveraddress) + "android_uploadcredit.php");
    }
//    public void initiateappend(){
//        Ajax ajax = new Ajax();
//        ajax.setCustomObjectListener(new Ajax.MyCustomObjectListener() {
//            @Override
//            public void onsuccess(String data) {
//                msgbox.show("",data);
//            }
//            @Override
//            public void onerror(){
//
//            }
//        });
//        JSONArray thejs = new JSONArray();
//        Cursor row = mydatabase.rawQuery("select b.payrollno,b.name,b.emp_id,b.emp_pins from employeeitems a,employeemasterfile b where b.emp_pins = a.employeeid group by a.employeeid",null);
//        DecimalFormat df = new DecimalFormat("###0.00");
//        while(row.moveToNext()){
//            String payrollnumber=row.getString(0);
//            String empname=row.getString(1);
//            String empid=row.getString(2);
//            String employeeid=row.getString(3);
//            String emppins=row.getString(3);
//            double totalamount=0.0;
//            JSONObject thejs2 = new JSONObject();
//            Cursor row2 = mydatabase.rawQuery("select (a.quantity * c.cost) as totalamount from employeeitems a, ecform c where a.itemid=c.id and a.employeeid="+employeeid,null);
//            try{
//                thejs2.put("payrollno",payrollnumber);
//                thejs2.put("empname",empname);
//                thejs2.put("empid",empid);
//                thejs2.put("emppins",emppins);
//                thejs2.put("transactionnumber",gettransactionnumber());
//                thejs2.put("inchargeid",globalvars.get("inchargeid"));
//                while(row2.moveToNext()){
//                    totalamount += Double.parseDouble(row2.getString(0));
//                }
//                thejs2.put("totalamount",df.format(totalamount));
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//            thejs.put(thejs2);
//        }
//        ajax.adddata("test",thejs.toString());
//        ajax.execute(this.getString(R.string.serveraddress) + "appendtotextfile.php");
//    }
    public void setfontawesome(int id,String icon){
        TextView txtview = (TextView) findViewById(id);
        txtview.setTypeface(font);
        txtview.setText(icon);
    }
    public String gettransactionnumber(){
        Cursor row = mydatabase.rawQuery("select thenumber from transactionnumber",null);
        row.moveToFirst();
        return row.getString(0);
    }
}
