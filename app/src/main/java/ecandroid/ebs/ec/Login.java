package ecandroid.ebs.ec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;

public class Login extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    Globalvars globalvars;
    Ajax mo;
    String temptext="";
    CharSequence txtusername,txtpassword;
    Msgbox msgbox;
    EditText txtviewpassword,txtviewusername;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnlogin = (Button) findViewById(R.id.btnlogin);
        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");

        globalvars = new Globalvars((Context)this,(Activity)this);
        //globalvars.set("testing","testing____");
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        createdatabases();
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY," +
                "emp_id TEXT," +
                "username TEXT," +
                "password TEXT," +
                "emp_no TEXT," +
                "emp_pins TEXT," +
                "emp_name TEXT," +
                "usertype TEXT)");

        msgbox = new Msgbox(this);
        //msgbox.show("t",globalvars.get("testing"));
        TextView txtview = (TextView) findViewById(R.id.txtusername);
        txtusername = txtview.getText();

        txtview = (TextView) findViewById(R.id.textviewuesrname);
        txtview.setTypeface(font);
        txtview.setText("\uf007");

        txtview = (TextView) findViewById(R.id.txtpassword);
        txtpassword = txtview.getText();

        txtview = (TextView) findViewById(R.id.textviewpassword);
        txtview.setTypeface(font);
        txtview.setText("\uf023");

        txtviewpassword = (EditText) findViewById(R.id.txtpassword);
        txtviewusername = (EditText) findViewById(R.id.txtusername);

        //txtenteremployeeid.setText("201803328935");

        txtviewpassword.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if(keyCode == 66 && event.getAction()==0){
                    temptext = txtviewpassword.getText().toString();
                    checklogin();
                }
                return false;
            }
        });

        final ProgressDialog pd = new ProgressDialog(Login.this);
        pd.setMessage("Updating users. Please wait...");
        pd.show();
        mo = new Ajax();
        mo.setCustomObjectListener(new Ajax.MyCustomObjectListener(){
            @Override
            public void onerror() {
                Toast toast = Toast.makeText(getApplicationContext(),"Users update failed. Please check your connection.",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
                toast.show();
                pd.dismiss();
            }
            @Override
            public void onsuccess(String data) {
                JSONArray thedata;
                //msgbox.show("",data);
                try {
                    thedata = new JSONArray(data);
                    String username,password,emp_id,emp_no,emp_pins,emp_name,usertype;
                    mydatabase.execSQL("DELETE FROM users");
                    for(int a=0;a<thedata.length();a++){
                        ContentValues cv = new ContentValues();
                        JSONArray row = thedata.getJSONArray(a);
                        username = row.getString(0);
                        password = row.getString(1);
                        emp_id = row.getString(2);
                        emp_no = row.getString(3);
                        emp_pins = row.getString(4);
                        emp_name = row.getString(5);
                        usertype = row.getString(6);

                        cv.put("emp_id",emp_id);
                        cv.put("username",username);
                        cv.put("password",password);
                        cv.put("emp_no",emp_no);
                        cv.put("emp_pins",emp_pins);
                        cv.put("emp_name",emp_name);
                        cv.put("usertype",usertype);
                        mydatabase.insert("users",null,cv);
                    }
                    pd.dismiss();

                    Toast toast = Toast.makeText(getApplicationContext(),"Users updated!",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
                    toast.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mo.execute(this.getString(R.string.serveraddress) + "android_downloadusers.php");

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Cursor resultSet = mydatabase.rawQuery("select count(username),password,emp_id from users "+
                                                        "where username="+qttext(txtusername.toString())+
                                                                " and password="+qttext(MD5(txtpassword.toString())),null);
                if(resultSet != null){
                    resultSet.moveToNext();
                    String count = resultSet.getString(0);
                    if(Integer.parseInt(count) > 0){
                        String inchargeempid = resultSet.getString(2);
                        globalvars.set("inchargeempid",inchargeempid);
                        startActivity(new Intent(Login.this,Home.class));
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Invalid Username/Password",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
                        toast.show();
                    }
                }
            }
        });
    }
    public void checklogin(){
        String emp_no = txtviewusername.getText().toString();
        String emp_pins = txtviewpassword.getText().toString();
        String query = "SELECT emp_name,usertype,emp_id FROM `users` " +
                        "where (emp_no like '%"+emp_no+"%' or '"+emp_no+"' like ('%'||emp_no||'%')) " +
                        "and (emp_pins like '%"+emp_pins+"%' or '"+emp_pins+"' like ('%'||emp_pins||'%'));";
        Cursor resultSet = mydatabase.rawQuery(query,null);
        if(resultSet != null){
            resultSet.moveToNext();
            String emp_name = resultSet.getString(0);
            final String usertype = resultSet.getString(1);
            final String emp_id = resultSet.getString(2);
            msgbox.showyesno("Login as ",emp_name);
            msgbox.setMsgboxListener(new Msgbox.MsgboxListener() {
                @Override
                public void onyes(){
                    if(usertype.equalsIgnoreCase("3")){
                        String inchargeempid = emp_id;
                        globalvars.set("inchargeempid",inchargeempid);
                        startActivity(new Intent(Login.this,Home.class));
                    }
                    else if(usertype.equalsIgnoreCase("4")){
                        startActivity(new Intent(Login.this,Supervisorhome.class));
                    }
                }
                @Override
                public void onno() {
                    txtviewusername.setText("");
                    txtviewpassword.setText("");
                    txtviewusername.requestFocus();
                }
            });
        }
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
    public void createdatabases(){
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS employeemasterfile(id INTEGER PRIMARY KEY, " +
                "emp_no TEXT, " +
                "emp_pins TEXT, " +
                "name TEXT, " +
                "aggregate_con TEXT, " +
                "aggregate_reg TEXT, " +
                "companyname TEXT, " +
                "businessunit TEXT, " +
                "deptname TEXT, " +
                "payrollno TEXT, " +
                "emp_id TEXT" +
                ")");

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS ecform(id INTEGER PRIMARY KEY," +
                "code TEXT," +
                "description TEXT," +
                "qty TEXT," +
                "unit TEXT," +
                "cost TEXT," +
                "expirydate TEXT," +
                "startdate TEXT," +
                "enddate TEXT," +
                "itemid TEXT"+
                ")");

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS employeeitems(id INTEGER PRIMARY KEY," +
                "emp_id TEXT," +
                "itemid TEXT," +
                "quantity TEXT," +
                "requestgroup TEXT," +
                "approvedby TEXT," +
                "requestdate TEXT)");

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS transactionnumber(id INTEGER PRIMARY KEY," +
                "thenumber TEXT)");

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS levelingsubordinates(id INTEGER PRIMARY KEY," +
                "ratee TEXT," +
                "subordinates_rater TEXT)");

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS inchargeid(id INTEGER PRIMARY KEY," +
                "idfront TEXT," +
                "idback TEXT)");

        mydatabase.execSQL("CREATE INDEX IF NOT EXISTS empnames ON employeemasterfile(emp_pins,name,companyname,businessunit,deptname)");
        mydatabase.execSQL("CREATE INDEX IF NOT EXISTS theforms ON ecform(code,description,qty,unit,cost,expirydate,startdate,enddate,itemid)");
        //mydatabase.execSQL("INSERT INTO transactionnumber(thenumber) VALUES("+qttext("1021201701")+")");
    }
}
