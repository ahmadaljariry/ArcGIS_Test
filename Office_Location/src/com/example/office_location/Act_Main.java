package com.example.office_location;

import java.util.ArrayList;

import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Field;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Act_Main extends ListActivity implements OnClickListener {

	ArcGISFeatureLayer featureLayer;
	Field[] fields;
	String targetServerURL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_act__main);
		targetServerURL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Specialty/ESRI_StateCityHighway_USA/MapServer";
		
		featureLayer = new ArcGISFeatureLayer(targetServerURL.concat("/2"),MODE.SELECTION);
		//if (featureLayer.isInitialized()) {

	    //	fields = featureLayer.getFields();

	   // } 
		
		String targetLayer2 = targetServerURL.concat("/2");
		String[] queryParams2 = { targetLayer2, "OBJECTID > 0" };
		AsyncQueryTask ayncQuery2 = new AsyncQueryTask();
		ayncQuery2.execute(queryParams2);
		
		Button bn = (Button) findViewById(R.id.bnt_Search);
		bn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				EditText tv = (EditText) findViewById(R.id.txt_CompanyName);
				
				//CNTYNAME like '%Clark%'
				String targetLayer2 = targetServerURL.concat("/2");
				String[] queryParams2 = { targetLayer2, "STATE_NAME like '%" + tv.getText() + "%'" };
				AsyncQueryTask ayncQuery2 = new AsyncQueryTask();
				ayncQuery2.execute(queryParams2);
				
			}
		});
		
		
		
	}
	
	FeatureSet featureSet;
	ProgressDialog progress;
	ArrayList<String> officename = new ArrayList<String>();
	String[] Officenames;
	private class AsyncQueryTask extends AsyncTask<String, Void, FeatureSet> {

		protected void onPreExecute() {
			progress = ProgressDialog.show(Act_Main.this, "",
					"Please wait....Loading Companies Name list");

		}

		/**
		 * First member in parameter array is the query URL; second member is
		 * the where clause.
		 */
		protected FeatureSet doInBackground(String... queryParams) {
			if (queryParams == null || queryParams.length <= 1)
				return null;

			String url = queryParams[0];
			Query query = new Query();
			String whereClause = queryParams[1];
			query.setOutFields(new String[] { "*" });
			SpatialReference sr = SpatialReference.create(4326);
			//query.setGeometry(new Envelope(2.00375072295943E7,1.99718688804086E7,2.00375072295943E7,1.99718688804086E7));
			query.setOutSpatialReference(sr);
			query.setReturnGeometry(true);
			query.setWhere(whereClause);
			
			QueryTask qTask = new QueryTask(url);
			FeatureSet featureSet = null;

			try {
				featureSet = qTask.execute(query);
			} catch (Exception e) {
				e.printStackTrace();
				return featureSet;
			}
			return featureSet;

		}
		String message = "No result comes back";
	
		
		protected void onPostExecute(FeatureSet result) {

			
			if (result != null) {
				
				
				featureSet = result;
				
				AttributeItem row = null;
				row = new AttributeItem();
				int cc = featureSet.getGraphics().length;
				
				if (featureLayer.isInitialized()) {

			    	fields = featureLayer.getFields();

			    } 
				officename.clear();
				
				for (int i=0;i<cc;i++){
				
					row.setField(fields[3]);
					Object value = featureSet.getGraphics()[i].getAttributeValue(fields[3].getName());
					row.setValue(value);
					officename.add(row.getValue().toString());
				
				}
				
				Officenames = new String[officename.size()]; 
				Officenames = officename.toArray(Officenames);
				
				callListAdapter();
				
	              

			}
			progress.dismiss();

			

		}

	}

	public void callListAdapter(){
		
		setListAdapter(new MyAdapter(this,
				android.R.layout.simple_list_item_1,R.id.textView1,
				Officenames));
		
	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		String[] items = new String[officename.size()]; 
		items = officename.toArray(items);
		
		Intent intent = new Intent(Act_Main.this, officeaddress.class);
		intent.putExtra("CompanyName", items[position].toString());
		startActivity(intent);
		
		
		
	}



	private class MyAdapter extends ArrayAdapter{

		public MyAdapter(Context context, int resource, int textViewResourceId,
				String[] strings) {
			super(context, resource, textViewResourceId, strings);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.list_items, parent, false);
			
			try{
			
			String[] items = new String[officename.size()]; 
			items = officename.toArray(items);//getResources().getStringArray(R.array.countries);
			
			
			ImageView iv = (ImageView) row.findViewById(R.id.imageViewListview);
			TextView tv = (TextView) row.findViewById(R.id.textView1);
			
			tv.setText(items[position]);
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			
			return row;
		}
	
	}
	

	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.act__main, menu);
		return true;
	}




	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	

	
	public class AttributeItem {

		  private Field field;

		  private Object value;

		  private View view;

		  public View getView() {

		    return view;
		  }

		  public void setView(View view) {

		    this.view = view;
		  }

		  public Field getField() {

		    return field;
		  }

		  public void setField(Field field) {

		    this.field = field;
		  }

		  public Object getValue() {

		    return value;
		  }

		  public void setValue(Object value) {

		    this.value = value;
		  }

		}



}
