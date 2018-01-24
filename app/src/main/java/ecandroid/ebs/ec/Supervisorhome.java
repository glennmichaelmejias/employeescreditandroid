package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Supervisorhome extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    Lstsupervisortransactionlist adapter;
    Msgbox msgbox;
    ListView myList;
    View v;
    Globalvars globalvars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisorhome);
        msgbox = new Msgbox((Context)this);
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        displayemployeelogs();
        CheckBox selectall = (CheckBox) findViewById(R.id.supervisorchkselecall);
        myList = (ListView) findViewById(R.id.lstviewsupervisor);
        Button btnapprove = (Button) findViewById(R.id.supervisorbtnapprove);
        globalvars = new Globalvars((Context)this,(Activity)this);
        selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                CheckBox et;
                for (int i = 0; i < myList.getCount(); i++){
                    v = myList.getChildAt(i);
                    et = (CheckBox) v.findViewById(R.id.supervisorchkselect);
                    et.setChecked(isChecked);
                }
            }
        });
        btnapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox et;
                for (int i = 0; i < myList.getCount(); i++){
                    v = myList.getChildAt(i);
                    et = (CheckBox) v.findViewById(R.id.supervisorchkselect);
                    if(et.isChecked()){
                        String emp_id = et.getTag().toString();
                        mydatabase.execSQL("update employeeitems set approvedby="+qttext(globalvars.get("inchargeempid"))+" where emp_id="+qttext(emp_id));
                    }
                }
                displayemployeelogs();
            }
        });

    }
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
    public void displayemployeelogs(){
        ListView lstlogs = (ListView) findViewById(R.id.lstviewsupervisor);
        ArrayList<HashMap<String, String>> detailss = new ArrayList<HashMap<String, String>>();
        Cursor row = mydatabase.rawQuery("select b.name," +
                "(b.companyname||'/'||b.businessunit||'/'||b.deptname) as department," +
                "b.emp_id as emp_id," +
                "ifnull(a.approvedby,'null') " +
                "from employeeitems a,employeemasterfile b " +
                "where b.emp_id = a.emp_id " +
                "group by a.emp_id",null);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setMaximumFractionDigits(2);
        while(row.moveToNext()){
            String employeename=row.getString(0);
            String department=row.getString(1);
            String emp_id=row.getString(2);
            String approvedby=row.getString(3);
            String thestatus = "";
            //msgbox.show("asdf",approvedby);
            if(approvedby.equalsIgnoreCase("null")){
               thestatus="Pending";
            }
            else{
                thestatus="Approved";
            }
            //String credititemid=row.getString(3);
            double totalamount=0.0;
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("employeename",employeename);
            map.put("department",department);
            Cursor row2 = mydatabase.rawQuery("select (a.quantity * c.cost) as totalamount from employeeitems a, ecform c where a.itemid=c.id and a.emp_id="+qttext(emp_id),null);
            while(row2.moveToNext()){
                totalamount += Double.parseDouble(row2.getString(0));
            }
            map.put("totalamount","Php " + df.format(totalamount)+"");
            map.put("emp_id",emp_id);
            map.put("status",thestatus);
            detailss.add(map);
        }
        adapter = new Lstsupervisortransactionlist (this,detailss);
        lstlogs.setAdapter(adapter);
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }

}
