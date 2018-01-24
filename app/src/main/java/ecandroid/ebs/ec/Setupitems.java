package ecandroid.ebs.ec;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Setupitems extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    Context context = this;
    Msgbox msgbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setupitems);
        msgbox = new Msgbox(context);
        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);
        Button btnaddnewitem = (Button) findViewById(R.id.btnaddnewitem);
        btnaddnewitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setupitems.this,Addnewitems.class));
            }
        });
        Button btndone = (Button) findViewById(R.id.btndone);
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Items saved!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listview) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.popup_menu, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.edit:
                return true;
            case R.id.delete:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        String[] from = new String[] {"prd_name","prd_name2"};
        int[] to = new int[] {R.id.tvProdukName,R.id.textView2};
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        Cursor resultSet = mydatabase.rawQuery("Select description,qty,unit,cost from ecform",null);
        if(resultSet != null){
            while(resultSet.moveToNext()){
                String itemdescription = resultSet.getString(0);
                String itemqty = resultSet.getString(1);
                String itemunit = resultSet.getString(2);
                String itemcost = resultSet.getString(3);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("prd_name", itemdescription);
                map.put("prd_name2", "Quantity: " + itemqty + "\t\t\t" + "Unit: " + itemunit + "\t\t\t" + "Unit Cost: " + itemcost);
                fillMaps.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.list_row, from, to);
            final ListView listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(adapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    msgbox.show(id,"asdf");
                    return false;
                }
            });
            registerForContextMenu(listView);
        }
    }
}
