package ecandroid.ebs.ec;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
public class Globalvars{
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public Globalvars(Context context,Activity act){
        preferences = act.getSharedPreferences("Global_vars",context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public void set(String varname, String varval){
        editor.putString(varname,varval);
        editor.commit();
    }
    public String get(String varname){
        return preferences.getString(varname,null);
    }
}
