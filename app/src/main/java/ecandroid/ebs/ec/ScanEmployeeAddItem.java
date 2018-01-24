package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScanEmployeeAddItem extends AppCompatActivity{
    SQLiteDatabase mydatabase;
    Context context=this;
    Msgbox msgbox;
    String selecteditemid;
    String employeeid;
    customlistscanemployee3additem adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_employee_add_item);

        final ListView listView = (ListView) findViewById(R.id.lstviewscanemployeeadditem);

        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        msgbox = new Msgbox(context);
        employeeid = getIntent().getStringExtra("employeeid");

        final ArrayList<String> theitems = new ArrayList<String>();

        String[] from = new String[]{"title","subtitle"};
        int [] to = new int[]{R.id.txtviewscanemployeetitle,R.id.txtviewscanemployeesubtitle};
        ArrayList<HashMap<String,String>> fillMaps = new ArrayList<HashMap<String,String>>();

        Cursor resultSet = mydatabase.rawQuery("Select description,qty,unit,cost,id from ecform",null);
        if(resultSet != null){
            while(resultSet.moveToNext()){
                String itemdescription = resultSet.getString(0);
                String itemqty = resultSet.getString(1);
                String itemunit = resultSet.getString(2);
                String itemcost = resultSet.getString(3);
                String itemid = resultSet.getString(4);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("title", itemdescription);
                map.put("subtitle", "Quantity: " + itemqty + "\t\t\t" + "Unit: " + itemunit + "\t\t\t" + "Unit Cost: " + itemcost);
                map.put("id",itemid);
                fillMaps.add(map);
                theitems.add(itemid);
            }
            //SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.scanemployeeadditemlist, from, to);
            //final ImageView imgpreview = (ImageView) findViewById(R.id.imgviewscanemployeeadditempreview);
            //final RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlscanemployeeadditempreviewgroup);
            //rl.setVisibility(View.VISIBLE);
            //rl.animate().alpha(0.0f);
            adapter = new customlistscanemployee3additem(this,fillMaps);
            adapter.setonimgclicklistener(new customlistscanemployee3additem.listener() {
                @Override
                public void onimgclick(String id) {
                    //imgpreview.setImageDrawable(Drawable.createFromPath("sdcard/ecimages/"+id+".jpg"));
                   //rl.setVisibility(View.VISIBLE);
                    //rl.animate().alpha(1.0f);
                    Intent newintent = new Intent(getBaseContext(),ImagePreview.class);
                    newintent.putExtra("id",id);
                    startActivity(newintent);
                }
            });

            listView.setAdapter(adapter);
            adapter.imgondestroy();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    selecteditemid = theitems.get(position);
                    //Intent returnIntent = new Intent();
                    //returnIntent.putExtra("result",selecteditemid);
                   // setResult(RESULT_OK,returnIntent);
                    additemtoemployee(selecteditemid);
                    finish();

                }
            });
        }
    }
    public void additemtoemployee(String itemid){
        mydatabase.execSQL("INSERT INTO employeeitems(emp_id,itemid,quantity) VALUES("+qttext("walapa")+","+qttext(itemid)+","+qttext(0)+")");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
}
