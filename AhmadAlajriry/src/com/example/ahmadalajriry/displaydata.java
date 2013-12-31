package com.example.ahmadalajriry;

import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Field;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class displaydata  extends ListActivity implements OnClickListener {
	MapView mMapView;	
	//FeatureSet featureSet;
	ArcGISTiledMapServiceLayer Dynamiclayer;
	String targetServerURL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/PublicSafety/PublicSafetyBasemap/MapServer";
	//ArcGISFeatureLayer featureLayer;
	ListView listView;
	ArcGISFeatureLayer featureLayer;
	
	Field[] fields;
	int[] editableFieldIndexes;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dispalydata);
		
		mMapView = (MapView) findViewById(R.id.map);
		Dynamiclayer =  new ArcGISTiledMapServiceLayer(targetServerURL);
		
		// initExtent = "-200.933001758447,-99,198.139666927391,99"
		
		//Envelope env = new Envelope(-200.933001758447,-99,198.139666927391,99);
		//mMapView.setExtent(env);
		
		
		mMapView.addLayer(Dynamiclayer);
		
		featureLayer = new ArcGISFeatureLayer(
				"http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/PublicSafety/PublicSafetyBasemap/MapServer/42",
				MODE.SELECTION);
		//setContentView(mMapView);
		mMapView.addLayer(featureLayer);
	    SimpleFillSymbol sfs = new SimpleFillSymbol(Color.TRANSPARENT);
	    sfs.setOutline(new SimpleLineSymbol(Color.YELLOW, 3));
	    featureLayer.setSelectionSymbol(sfs);
		
	    //this.editableFieldIndexes = FeatureLayerUtils.createArrayOfFieldIndexes(this.fields);
	    //listView = (ListView) findViewById(R.id.list_items);
	    if (featureLayer.isInitialized()) {

	    	fields = featureLayer.getFields();

	    } 
	    
	   
	    
	  	String targetLayer2 = targetServerURL.concat("/45");
		String[] queryParams2 = { targetLayer2, "OBJECTID > 0" };
		AsyncQueryTask ayncQuery2 = new AsyncQueryTask();
		ayncQuery2.execute(queryParams2);
		
	}
	FeatureSet featureSet;
	ProgressDialog progress;
	//AttributeListAdapter listAdapter;
	private class AsyncQueryTask extends AsyncTask<String, Void, FeatureSet> {

		protected void onPreExecute() {
			progress = ProgressDialog.show(displaydata.this, "",
					"Please wait....query task is executing");

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
	
		ArrayList<String> cityname = new ArrayList<String>();
		protected void onPostExecute(FeatureSet result) {

			
			if (result != null) {
				
				//message = (String) result.getGraphics()[0].getAttributeValue(featureLayer.getObjectIdField());
				featureSet = result;
				
				AttributeItem row = null;
				row = new AttributeItem();
				int cc = featureSet.getGraphics().length;
				
				
				for (int i=0;i<cc;i++){
				
					row.setField(fields[1]);
					Object value = featureSet.getGraphics()[i].getAttributeValue(fields[1].getName());
					row.setValue(value);
					cityname.add(row.getValue().toString());
				
				}
				
				
				
				callListAdapter();
				// set new data and notify adapter that data has changed
	              //listAdapter.setFeatureSet(result);
	              //listAdapter.notifyDataSetChanged();
	              //listView.setAdapter(listAdapter);
				//AttributeItem row = null;
				//row = new AttributeItem();
				//int cc = result.getGraphics().length;
				

				
				//for (int i=0;i<8;i++){
				
				//	row.setField(fields[1]);
				//	Object value = result.getGraphics()[i].getAttributeValue(fields[1].getName());
				//	row.setValue(value);
				
				//}
				//message = row.getValue().toString();
				
				//message = result.getDisplayFieldName();
				//result.getFields().;
				//int i = 0 ;
				
				//for(int position=0;position<result.getGraphics().length;position++){ 
				//	AttributeItem item = (AttributeItem) getItem(position);

				   
				  

				//    if (FieldType.determineFieldType(item.getField()) == FieldType.STRING) {

				        // get the string specific layout
				    
				//    	cityName[position] = item.getValue().toString();

				//    } 

				  
				
				//}
				
				
				
	              

			}
			progress.dismiss();

			//Toast toast = Toast.makeText(displaydata.this, message,
			//		Toast.LENGTH_LONG);
			//toast.show();
			//queryButton.setText("Clear graphics");
			//boolQuery = false;

		}

	}
	
	public void callListAdapter(){
		
		 setListAdapter(new MyAdapter(this,
			      	android.R.layout.simple_list_item_1,R.id.field_value_txt,
			      	getResources().getStringArray(R.array.countries)));
		
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
			View row1 = inflater.inflate(R.layout.item_text, parent, false);
			//String[] items = getResources().getStringArray(R.array.countries);
			
			
			
		
			
			
			TextView tv = (TextView) findViewById(R.id.field_value_txt);
			
			//tv.setText();
			
			
			
			return row1;
		}
	
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		
		
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


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	

}
