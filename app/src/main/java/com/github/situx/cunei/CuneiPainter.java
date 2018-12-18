package com.github.situx.cunei;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Main activity enabling the drawing of cuneiform characters.
 */
public class CuneiPainter extends Activity {

    private Map<String,List<Tuple<String,String>>> lookUpMap;
    private TextView statusText;
    private DrawingCanvas mv;

    private ListView resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.lookUpMap=new TreeMap<>();
        statusText = findViewById(R.id.startView);
        this.mv = findViewById(R.id.imageView);
        this.resultList = findViewById(R.id.groupListView);
        try {
            this.jsonToMap(this.loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.setStatusText();

        mv.setDrawingCacheEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.menu_exit:
                this.finish();
                return true;
            case R.id.menu_about:

        final Dialog dialog = new Dialog(this); // context, this etc.
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("About CuneiPainter...");
        TextView viewText = dialog.findViewById(R.id.dialog_info);
        viewText.setText(Html.fromHtml("<html>Written by Timo Homburg<br>Licensed under GPLv3<br>Includes the Akkadian Font provided by <a href=\"http://users.teilar.gr/~g1951d/\">George Douros</a></html>"));
        Button button=dialog.findViewById(R.id.dialog_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog.cancel();
            }
        });
        dialog.show();
                break;

        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                this.mv.clearCanvas();
                this.clear();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.mv.undoPath();

    }

    /**
     * Clears the canvas and resets search parameters.
     */
    private void clear(){
        this.mv.a=0;
        this.mv.b=0;
        this.mv.c=0;
        this.mv.d=0;
        this.mv.s=0;
        this.mv.down=0;
        this.mv.up=0;
        this.mv.left=0;
        this.mv.right=0;
        this.mv.paleocodage="";
        this.resultList.setAdapter(new ResultAdapter(this,new LinkedList<Tuple<String,String>>()));
        this.setStatusText();
    }

    /**
     * Sets the status text indicating the four strokes.
     */
    void setStatusText(){
    this.statusText.setText("A: "+this.mv.a+" B: "+this.mv.b+" C: "+this.mv.c+" D: "+this.mv.d+" Strokes: "+this.mv.s+" Paleocodage: "+this.mv.paleocodage);
    }

    /**
     * Looks up corresponding characters after canvas input.
     */
    void lookUp(){
        StringBuilder queryStr=new StringBuilder();
        if(this.mv.a==0 && this.mv.b==0 && this.mv.c==0 && this.mv.d==0){
            this.resultList.setAdapter(new ResultAdapter(this,new LinkedList<Tuple<String,String>>()));
        }else {
            if (this.mv.a > 0) {
                queryStr.append("a").append(this.mv.a);
            }
            if (this.mv.b > 0) {
                queryStr.append("b").append(this.mv.b);
            }
            if (this.mv.c > 0) {
                queryStr.append("c").append(this.mv.c);
            }
            if (this.mv.d > 0) {
                queryStr.append("d").append(this.mv.d);
            }
            this.resultList.setAdapter(new ResultAdapter(this, this.lookUpMap.get(queryStr.toString()
            )));
        }
    }

    /**
     * Loads character descriptions from the JSON set.
     * @return the json to be returned.
     */
    private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("strokes_gott.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    /**
     * Maps the character json representation to an internal data structure.
     * @param t the json string
     * @throws JSONException if the JSON file is errornous
     */
    private void jsonToMap(String t) throws JSONException {
        System.out.println("T: "+t);
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key);
            JSONArray jvalue = new JSONArray(value);
            this.lookUpMap.put(key,new LinkedList<Tuple<String, String>>());
            for (int i = 0; i < jvalue.length(); i++) {
                JSONArray row = jvalue.getJSONArray(i);
                this.lookUpMap.get(key).add(new Tuple<>(row.get(0).toString(),row.get(1).toString()));
            }
        }
    }
}
