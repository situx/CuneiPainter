package com.github.situx.cunei;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by situx on 5/23/15.
 * Adapter class for displaying cuneiform character results.
 */
public class ResultAdapter extends BaseAdapter {
    private final Typeface myTypeface;
    private List<Tuple<String,String>> items;

    private Context context;

    /**
     * Constructor for this class.
     * @param context the context of this adapter
     * @param items the items to handle
     */
    public ResultAdapter(final Context context,final List<Tuple<String,String>> items){
        this.items=items;
        this.context=context;
        this.items=items;
        this.myTypeface = Typeface.createFromAsset(this.context.getAssets(), "akkadian.ttf");
    }

    @Override
    public int getCount() {
        if(items==null){
            return 1;
        }
        return this.items.size();
    }

    @Override
    public Object getItem(final int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        TextView tview=new TextView(this.context);
        tview.setTypeface(this.myTypeface);
        tview.setTextSize(24);
        if(this.items!=null)
            tview.setText(ResultAdapter.this.items.get(i).getOne()+" ");
        tview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(ResultAdapter.this.items!=null)
                    ResultAdapter.this.openDialog(items.get(i).getOne(),items.get(i).getTwo());
            }
        });
        return tview;
    }

    /**
     * Opens a dialog window to display additional information about the cuneiform character.
     * @param title the title of the dialog window
     * @param text the text of the dialog window
     */
    private void openDialog(String title, String text) {
        final Dialog dialog = new Dialog(context); // context, this etc.
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(title);
        ((TextView)dialog.findViewById(android.R.id.title)).setTypeface(
                Typeface.createFromAsset(this.context.getAssets(), "akkadian.ttf"));
        TextView viewText = (TextView)dialog.findViewById(R.id.dialog_info);
        viewText.setTypeface(this.myTypeface);
        viewText.setText(Html.fromHtml(text));
        Button button=(Button)dialog.findViewById(R.id.dialog_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
