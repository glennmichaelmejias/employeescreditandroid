package ecandroid.ebs.ec;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImagePreview extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        ImageView img = (ImageView) findViewById(R.id.imgimagepreviewpreviewimage);
        String id = getIntent().getStringExtra("id");
        img.setImageDrawable(Drawable.createFromPath("sdcard/ecimages/"+id+".png"));
    }
}
