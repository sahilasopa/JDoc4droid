package io.github.danthe1st.jdoc4droid.activities.show.showclass;

import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.danthe1st.jdoc4droid.R;
import io.github.danthe1st.jdoc4droid.model.textholder.TextHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ShowSectionAdapter extends BaseAdapter {

    private final LayoutInflater inflater;

    @Getter
    @Setter
    private List<TextHolder> sections=new ArrayList<>();

    @Override
    public int getCount() {
        return sections.size();
    }

    @Override
    public Object getItem(int position) {
        return sections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.context_menu_item, parent, false);
        view.<TextView>findViewById(R.id.contextMenuField).setText(sections.get(position).getText());
        return view;
    }

    public int getPositionFromName(TextHolder name) {
        for (int i = 0; i < sections.size(); i++) {
            if(name.equals(sections.get(i))){
                return i;
            }
        }
        return -1;
    }
}
