package ecandroid.ebs.ec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanEmployee3Addnewtransaction extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    CustomItemsList adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanemployee3addnewtransaction);
        Button addnewitem = (Button) findViewById(R.id.btnaddnewtransactionadditem);
        Button done = (Button) findViewById(R.id.btnaddnewtransactiondone);
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);

        addnewitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanEmployee3Addnewtransaction.this,ScanEmployeeAddItem.class));
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanEmployee3Addnewtransaction.this,ScanEmployee3Details.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayemployeeitems();
    }

    public void displayemployeeitems(){
        ListView lstviewddeditems = (ListView) findViewById(R.id.lstviewaddnewtransactionaddeditems);
        lstviewddeditems.setFocusable(false);
        Cursor row = mydatabase.rawQuery("select ecform.description,employeeitems.quantity,ecform.cost,ecform.unit,employeeitems.id from employeeitems,ecform where employeeitems.itemid=ecform.id and employeeitems.emp_id="+qttext("walapa"),null);
        ArrayList<HashMap<String, String>> detailss = new ArrayList<HashMap<String, String>>();
        double totalamount=0.0;
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setMaximumFractionDigits(2);
        while(row.moveToNext()){
            String itemdescription=row.getString(0);
            String theqty = row.getString(1);
            String price = row.getString(2);
            String unit = row.getString(3);
            String employeeitemsid = row.getString(4);
            double myNumber = (Double.parseDouble(theqty) * Double.parseDouble(price));

            String amount =  df.format(myNumber);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("lsttitle", itemdescription);
            map.put("lstsubtitle", "Php "+price+" / "+unit);
            map.put("qty", theqty);
            map.put("employeeitemsid",employeeitemsid);
            map.put("amount",amount);
            detailss.add(map);
            totalamount += myNumber;
        }

        TextView txtamount = (TextView) findViewById(R.id.txtviewscanemployeeitemscredittotalamount);
        txtamount.setText("Php " + df.format(totalamount)+"");

        int index = lstviewddeditems.getFirstVisiblePosition();
        View v = lstviewddeditems.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - lstviewddeditems.getPaddingTop());

        adapter = new CustomItemsList(this, detailss);
        lstviewddeditems.setAdapter(adapter);
        lstviewddeditems.setSelectionFromTop(index, top);
    }
    private String m_Text = "";
    public void myClickHandler(View v) {
        final Button n = (Button) v;
        final String empid = n.getTag().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Quantity");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(n.getText());
        input.selectAll();
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                m_Text = input.getText().toString();
                mydatabase.execSQL("UPDATE employeeitems set quantity="+m_Text+" where id="+empid);
                displayemployeeitems();
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
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
}
