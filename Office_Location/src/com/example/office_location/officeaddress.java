package com.example.office_location;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class officeaddress extends Activity {
	MapView mMapView;
	ArcGISDynamicMapServiceLayer Dynamiclayer;
	String CompanyName;
	ProgressDialog progress;
	GraphicsLayer graphicsLayer;
	String targetServerURL = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/PublicSafety/PublicSafetyBasemap/MapServer/";
	//String targetServerURL = "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Petroleum/KSPetro/MapServer";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.officeaddress);

		CompanyName = getIntent().getExtras().getString("CompanyName");
		TextView tv = (TextView) findViewById(R.id.CompanyName);		
		tv.setText(CompanyName);
		
		mMapView = (MapView) findViewById(R.id.map);
		
		Dynamiclayer =  new ArcGISDynamicMapServiceLayer(targetServerURL);
		mMapView.addLayer(Dynamiclayer);
		
		graphicsLayer = new GraphicsLayer();
		SimpleRenderer sr = new SimpleRenderer(new SimpleFillSymbol(Color.GREEN));
		graphicsLayer.setRenderer(sr);
		mMapView.addLayer(graphicsLayer);
		
		String targetLayer = targetServerURL.concat("/42");
		String[] queryParams = { targetLayer, "CNTYNAME = '"+ CompanyName +"' " };
		AsyncQueryTask ayncQuery = new AsyncQueryTask();
		ayncQuery.execute(queryParams);
		
		//String targetLayer = targetServerURL.concat("/1");
		//String[] queryParams = { targetLayer, "FIELD_NAME = 'FOREST CITY COAL GAS AREA' " };
		//AsyncQueryTask ayncQuery = new AsyncQueryTask();
		//ayncQuery.execute(queryParams);
		
	}
	

	private class AsyncQueryTask extends AsyncTask<String, Void, FeatureSet> {

		protected void onPreExecute() {
			progress = ProgressDialog.show(officeaddress.this, "",
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
			SpatialReference sr = SpatialReference.create(4267);
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

		protected void onPostExecute(FeatureSet result) {

			String message = "No result comes back";
			if (result != null) {
				Graphic[] grs = result.getGraphics();

				if (grs.length > 0) {
					
					
						graphicsLayer.addGraphics(grs);
						message = (grs.length == 1 ? "1 result has " : Integer
							.toString(grs.length) + " results have ")
							+ "come back";
					
				}

			}
			progress.dismiss();

			Toast toast = Toast.makeText(officeaddress.this, message,
					Toast.LENGTH_LONG);
			toast.show();
			//queryButton.setText("Clear graphics");
			//boolQuery = false;

		}

	}
	
	
	
	

}
