package be.qrsdp.worktimer.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import be.qrsdp.utils.Util;
import be.qrsdp.worktimer.R;
import be.qrsdp.worktimer.R.id;
import be.qrsdp.worktimer.R.layout;
import be.qrsdp.worktimer.data.WorkDay;
import be.qrsdp.worktimer.data.WorkLog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
    private List<WorkDay> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<WorkDay, List<WorkLog>> _listDataChild;
 
    public ExpandableListAdapter(Context context, HashMap<WorkDay, List<WorkLog>> listChildData) {
        this._context = context;
        this._listDataHeader = new ArrayList<WorkDay>(listChildData.keySet());
        Collections.sort(_listDataHeader);
        //Collections.reverse(_listDataHeader);
        this._listDataChild = listChildData;
    }
 
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
    	WorkLog log = (WorkLog)getChild(groupPosition, childPosition);
        final String childText = log.getString()
        		+ " \t" + Util.getDurationString(log.getDuration());
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
 
        txtListChild.setText(childText);
        return convertView;
    }
 
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	
    	WorkDay day = (WorkDay) getGroup(groupPosition);
        String headerTitle = day.getString()
        		+ " \t" + Util.getDurationString(day.getDuration());
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
 
    public boolean hasStableIds() {
        return false;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
