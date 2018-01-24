package ecandroid.ebs.ec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import ecandroid.ebs.ec.R;

/**
 * Created by bluebook1 on 10/12/2017.
 */

public class CustomItemsList extends BaseAdapter{
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater=null;
    private SQLiteDatabase mydatabase;

    public interface customiteminterface{
        public void qtytextchange(String empitemsid);
    }

    public customiteminterface interfaceqtychange;
    Context ctx;
    public void setonqtychange(customiteminterface thecustom){
        this.interfaceqtychange = thecustom;
    }
    public CustomItemsList(Activity a, ArrayList<HashMap<String, String>> d){
        ctx = a.getBaseContext();
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mydatabase = activity.openOrCreateDatabase("db.db",activity.MODE_PRIVATE,null);
    }

    public int getCount()
    {
        return data.size();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.scanemployeelistrow, null);

        TextView title = (TextView)vi.findViewById(R.id.txtviewscanemployeeaddedtitle);
        TextView price = (TextView) vi.findViewById(R.id.txtviewscanemployeeaddedsubtitle);
        Button btnquantity = (Button) vi.findViewById(R.id.btnscanemployeelistrowsetquantity);
        TextView amount = (TextView) vi.findViewById(R.id.txtscanemployeelistrowamount);

        HashMap<String, String> category = new HashMap<String, String>();
        category = data.get(position);

        title.setText(category.get("lsttitle"));
        price.setText(category.get("lstsubtitle"));
        btnquantity.setText(category.get("qty"));
        amount.setText(category.get("amount"));

        String empitemsid = category.get("employeeitemsid");
        btnquantity.setTag(empitemsid);

        btnquantity.setFocusable(false);

        return vi;
    }

    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
}
