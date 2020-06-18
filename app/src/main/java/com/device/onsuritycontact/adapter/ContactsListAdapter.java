package com.device.onsuritycontact.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.device.onsuritycontact.R;
import com.device.onsuritycontact.activity.ContactDetailsActivity;
import com.device.onsuritycontact.model.ContactsModel;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends ArrayAdapter {
  private Context context;
  
  private List<ContactsModel> data = new ArrayList();
  
  private int layoutResourceId;
  
  public ContactsListAdapter(Context paramContext, int paramInt, List<ContactsModel> paramArrayList) {
    super(paramContext, paramInt, paramArrayList);
    this.layoutResourceId = paramInt;
    this.context = paramContext;
    this.data = paramArrayList;
  }
  
  public View getView(int position, View paramView, ViewGroup paramViewGroup) {
    ViewHolder viewHolder;
    View view = paramView;
    if (view == null) {
      view = ((Activity)this.context).getLayoutInflater().inflate(this.layoutResourceId, paramViewGroup, false);
      viewHolder = new ViewHolder();
      viewHolder.name = (TextView)view.findViewById(R.id.contact_name);
      view.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder)view.getTag();
    }

    final ContactsModel contactsModel = (ContactsModel) this.data.get(position);

    viewHolder.name.setText(contactsModel.getFirstName());

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, ContactDetailsActivity.class);
        intent.putExtra("contact", contactsModel);
        context.startActivity(intent);
      }
    });

    return view;
  }
  
  static class ViewHolder {
    TextView name;
  }
}
