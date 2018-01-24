package ecandroid.ebs.ec;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanEmployee3 extends AppCompatActivity {
    Lstscanemployee3viewlogs adapter;
    SQLiteDatabase mydatabase;
    Msgbox msgbox;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_employee3);
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        Button addnewtransaction = (Button) findViewById(R.id.btnscanemployee3addnewtransaction);
        msgbox = new Msgbox(context);
        addnewtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanEmployee3.this,ScanEmployee3Addnewtransaction.class));
            }
        });

    }
    protected void onResume(){
        super.onResume();
        displayemployeelogs();
    }
    public void displayemployeelogs(){
        ListView lstlogs = (ListView) findViewById(R.id.lstviewscanemployee3logs);
        ArrayList<HashMap<String, String>> detailss = new ArrayList<HashMap<String, String>>();
        Cursor row = mydatabase.rawQuery("select b.name,(b.companyname||'/'||b.businessunit||'/'||b.deptname) as department,b.emp_id as emp_id from employeeitems a,employeemasterfile b where b.emp_id = a.emp_id group by a.emp_id",null);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setMaximumFractionDigits(2);
        while(row.moveToNext()){
            String employeename=row.getString(0);
            String department=row.getString(1);
            String emp_id=row.getString(2);
            double totalamount=0.0;
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("employeename",employeename);
            map.put("department",department);
            Cursor row2 = mydatabase.rawQuery("select (a.quantity * c.cost) as totalamount from employeeitems a, ecform c where a.itemid=c.id and a.emp_id="+qttext(emp_id),null);
            while(row2.moveToNext()){
                totalamount += Double.parseDouble(row2.getString(0));
            }
            map.put("totalamount","Php " + df.format(totalamount)+"");
            detailss.add(map);
        }
        adapter = new Lstscanemployee3viewlogs(this,detailss);
        lstlogs.setAdapter(adapter);
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
}
