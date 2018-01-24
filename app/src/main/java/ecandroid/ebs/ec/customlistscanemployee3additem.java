package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bluebook1 on 10/24/2017.
 */

public class customlistscanemployee3additem extends BaseAdapter{
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater=null;
    private Msgbox msgbox;
    ImageView imgview;
    Context ctx;
    public interface listener{
        public void onimgclick(String id);
    }
    public listener listener;
    public void setonimgclicklistener(listener lst){this.listener = lst;}
    public customlistscanemployee3additem(Activity a, ArrayList <HashMap<String,String>> d){
        ctx = a.getBaseContext();
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        msgbox = new Msgbox((Context)a);
    }
    public void imgondestroy(){
        //if(!imgview)
        //imgview.setImageDrawable(null);
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public Object getItem(int position){return position;}
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View vi=convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.scanemployeeadditemlist, null);

        TextView title = (TextView)vi.findViewById(R.id.txtviewscanemployeetitle);
        TextView subtitle = (TextView)vi.findViewById(R.id.txtviewscanemployeesubtitle);
        imgview = (ImageView)vi.findViewById(R.id.imgviewscanemployeeimage);

        HashMap<String,String> lst = new HashMap<String,String>();

        lst = data.get(position);

        title.setText(lst.get("title"));
        subtitle.setText(lst.get("subtitle"));
        File theimg = new File("sdcard/ecimages/"+lst.get("id")+".png");
        if(theimg.exists())
            imgview.setImageDrawable(Drawable.createFromPath("sdcard/ecimages/"+lst.get("id")+".png"));
        //Bitmap bitmap = BitmapFactory.decodeFile(theimg.getAbsolutePath());
        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 1, out);
        //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

        //imgview.setImageBitmap(decoded);
           //
        final String theid = lst.get("id");
        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //msgbox.show("asdf",theid);
                listener.onimgclick(theid);
            }
        });
        return vi;
    }
}
