package ecandroid.ebs.ec;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

public class Addnewitems extends AppCompatActivity {
    SQLiteDatabase mydatabase;
    Button captureimage;
    ImageView camerapreview;
    static final int CAM_REQUEST = 1;
    Msgbox msgbox;
    File imgfile;
    File folder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewitems);

        msgbox = new Msgbox((Context) this);
        imgfile = new File(folder,"img.jpg");

        mydatabase = openOrCreateDatabase("db.db",MODE_PRIVATE,null);

        captureimage = (Button) findViewById(R.id.btnaddnewitemscaptureimage);
        camerapreview = (ImageView) findViewById(R.id.imgaddnewitemscamerapreview);

        final Spinner spinitemunit = (Spinner) findViewById(R.id.spinitemunit);
        String[] items = new String[]{"pcs", "pack","kilo","ctn","box","heads"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinitemunit.setAdapter(adapter);

        final EditText txtitemcode = (EditText) findViewById(R.id.txtitemcode);
        final EditText txtdescription = (EditText) findViewById(R.id.txtitemdescription);
        final EditText txtquantity = (EditText) findViewById(R.id.txtitemquantity);
        final EditText txtunitcostperitem = (EditText) findViewById(R.id.txtunitcostperitem);
        Button btnadditemsave = (Button) findViewById(R.id.btnadditemsave);

        btnadditemsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydatabase.execSQL("INSERT INTO ecform(code,description,qty,unit,cost)" +
                                    "VALUES(" + qttext(txtitemcode.getText()) + "," +
                                    qttext(txtdescription.getText()) + "," +
                                    qttext(txtquantity.getText())+"," +
                                    qttext(spinitemunit.getSelectedItem()) + "," +
                                    qttext(txtunitcostperitem.getText()) + ")");
                if(imgfile.exists()){
                    Cursor row = mydatabase.rawQuery("SELECT count(id) from ecform",null);
                    row.moveToFirst();
                    String lastid = row.getString(0);
                    imgfile.renameTo(new File(folder,lastid+".jpg"));
                }

//                Toast.makeText(getApplicationContext(),"New Item successfully added!"+"INSERT INTO ecform(code,description,qty,unit,cost)" +
//                        "VALUES(" + qttext(txtitemcode.getText()) + "," +
//                        qttext(txtdescription.getText()) + "," +
//                        qttext(txtquantity.getText())+"," +
//                        qttext(spinitemunit.getSelectedItem()) + "," +
//                        qttext(txtunitcostperitem.getText()) + ")",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        captureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getfile();
                camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera,CAM_REQUEST);
            }
        });
    }
    public String qttext(Object txt){
        return '"' + txt.toString() + '"';
    }
    private File getfile(){
        folder = new File("sdcard/ecimages");
        if(!folder.exists()){
            folder.mkdir();
        }
        imgfile = new File(folder,"img.jpg");
        return imgfile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "sdcard/ecimages/img.jpg";
        camerapreview.setImageDrawable(Drawable.createFromPath(path));
    }
}
