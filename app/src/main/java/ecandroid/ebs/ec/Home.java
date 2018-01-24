package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity{
    Typeface font;
    SQLiteDatabase mydatabase;
    Globalvars globalvars;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);

        font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        Button btndownloaddata = (Button) findViewById(R.id.btndownloaddata);
        Button btnsetupitems = (Button) findViewById(R.id.btnsetupitems);
        Button btnscanemployee = (Button) findViewById(R.id.btnscanemployee);
        Button btnappendtotextfiels = (Button) findViewById(R.id.btnappendtotextfile);
        setfontawesome(R.id.txtviewdownloaddata,"\uf0ed");
        setfontawesome(R.id.txtviewsetupitems,"\uf0cb");
        setfontawesome(R.id.txtviewstartscan,"\uf02a");
        setfontawesome(R.id.txtviewappendtotextfile,"\uf0ee");
        setfontawesome(R.id.txtviewgeneratereportsummary,"\uf022");
        setfontawesome(R.id.txtviewgeneratereportpercustomer,"\uf022");
        btndownloaddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Downloaddata.class));
            }
        });
        btnsetupitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Setupitems.class));
            }
        });
        btnscanemployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Home.this,ScanEmployee.class));
                startActivity(new Intent(Home.this,ScanEmployee3.class));
            }
        });
        btnappendtotextfiels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Appendtotextfiles.class));
            }
        });
    }

    public void setfontawesome(int id,String icon){
        TextView txtview = (TextView) findViewById(id);
        txtview.setTypeface(font);
        txtview.setText(icon);
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
}
