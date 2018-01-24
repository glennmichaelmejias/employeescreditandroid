package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanEmployee extends AppCompatActivity{
    Context context = this;
    String employeeid = "";
    String temptext="";
    Msgbox msgbox;
    Boolean newid = true;
    String firsttext="";
    SQLiteDatabase mydatabase;
    EditText txtenteremployeeid;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_employee);
        msgbox = new Msgbox(context);
        txtenteremployeeid = (EditText) findViewById(R.id.txtenteremployeeid);
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        final Button btnadditem = (Button) findViewById(R.id.btnscanemployeeadditem);
        final Button btnaffixsignature= (Button) findViewById(R.id.btnaffixsignature);
        btnadditem.setFocusable(false);
        btnaffixsignature.setFocusable(false);
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
                    displayemployeeitems();
                    txtenteremployeeid.requestFocus();
                }
                return false;
            }
        });

    }
    public void displayemployeeinfo(){
        if(employeeid!=""){
            Cursor resultSet = mydatabase.rawQuery("Select name,companyname,businessunit,deptname,payrollno from employeemasterfile where emp_pins="+employeeid,null);
            resultSet.moveToFirst();
            if(resultSet != null){
                String employeename = resultSet.getString(0);
                String department = resultSet.getString(1)+" / "+resultSet.getString(2)+" / "+resultSet.getString(3);
                String payrollnumber = resultSet.getString(4);
                TextView txtviewemployeename = (TextView) findViewById(R.id.txtviewemployeename);
                txtviewemployeename.setText(employeename);
                TextView txtviewdepartment = (TextView) findViewById(R.id.txtviewdepartment);
                txtviewdepartment.setText(department);
                TextView txtviewpayrollnumber = (TextView) findViewById(R.id.txtviewpayrollnumber);
                txtviewpayrollnumber.setText(payrollnumber);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                displayemployeeitems();
            }
        }
    }
    public void displayemployeeitems(){
        ListView lstviewddeditems = (ListView) findViewById(R.id.lstviewaddeditems);
        lstviewddeditems.setFocusable(false);
        Cursor row = mydatabase.rawQuery("select ecform.description,ecform.qty,ecform.cost,ecform.unit from employeeitems,ecform where employeeitems.itemid=ecform.id and employeeitems.employeeid="+employeeid,null);
        ArrayList<HashMap<String, String>> detailss = new ArrayList<HashMap<String, String>>();

        while(row.moveToNext()){
            String itemdescription=row.getString(0);
            String theqty = row.getString(1).replace(".","");
            String price = row.getString(2);
            String unit = row.getString(3);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("lsttitle", itemdescription);
            map.put("lstsubtitle", "Php "+price+" / "+unit);
            map.put("max", theqty);
            detailss.add(map);
        }
        CustomItemsList adapter = new CustomItemsList(this, detailss);
        lstviewddeditems.setAdapter(adapter);

    }
}
