package com.example.ahmadalajriry;


import java.util.ArrayList;
import java.util.List;



import com.esri.android.action.IdentifyResultSpinner;
import com.esri.android.action.IdentifyResultSpinnerAdapter;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.GraphicsLayer.RenderingMode;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.ags.ArcGISFeatureLayer.Options;
import com.esri.android.map.ags.ArcGISFeatureLayer.SELECTION_METHOD;
import com.esri.android.map.event.OnSingleTapListener;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.geometry.Unit.UnitType;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.SpatialRelationship;
import com.esri.core.tasks.ags.identify.IdentifyParameters;
import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.esri.core.tasks.ags.identify.IdentifyTask;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	MapView mMapView;
	ArcGISTiledMapServiceLayer basemapSetallite;
	ArcGISDynamicMapServiceLayer Dynamiclayer;
	EditText txt_Extent;
	Button queryButton;
	Button queryButton2;
	ProgressDialog progress;
	GraphicsLayer graphicsLayer;
	GraphicsLayer graphicsLayerpoint;
	String targetServerURL = "http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Petroleum/KSPetro/MapServer/";
	boolean Identifybool;
	boolean Bufferbool = false;
	IdentifyParameters params;

	boolean polyGraphics = false;
	boolean pointGraphics = false;
	// create UI objects
	static ProgressDialog dialog;
	ArcGISFeatureLayer fLayer = null;
	SeekBar bufferDistance = null;
	TextView bufferDistTextValue = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Petroleum/KSPetro/MapServer/
		//http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer
		mMapView = (MapView) findViewById(R.id.map);
		//basemapSetallite = new ArcGISTiledMapServiceLayer("https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer");
		//mMapView.addLayer(basemapSetallite);

		Dynamiclayer =  new ArcGISDynamicMapServiceLayer(targetServerURL);
		mMapView.addLayer(Dynamiclayer);
		
		//XMin: 102.049575703
		//YMin: 5.6843418860808E-14
		//XMax: 5.6843418860808E-14
		//YMax: 40.002644049
				
		
		//XMin: -98.5000473025051
		//YMin: 37.3553593835208
		//XMax: -97.6655703277604
		//YMax: 37.8866930261227
		//Spatial Reference: 4267
		// Set the envelope for the map
		Envelope env = new Envelope(-98.5000473025051,37.3553593835208,-97.6655703277604,37.8866930261227);
		mMapView.setExtent(env);
		
		
		graphicsLayer = new GraphicsLayer();
		SimpleRenderer sr = new SimpleRenderer(new SimpleFillSymbol(Color.GREEN));
		graphicsLayer.setRenderer(sr);
		mMapView.addLayer(graphicsLayer);
		
		graphicsLayerpoint = new GraphicsLayer();
		SimpleRenderer sr2 = new SimpleRenderer(new SimpleMarkerSymbol(Color.CYAN,10,SimpleMarkerSymbol.STYLE.CIRCLE));
		graphicsLayerpoint.setRenderer(sr2);
		mMapView.addLayer(graphicsLayerpoint);
		
		// Add the graphics layer for user to draw on the map Buffer
		firstGeomLayer = new GraphicsLayer();
		mMapView.addLayer(firstGeomLayer);
				
		// Add the graphics layer to display results on the map
		resultGeomLayer = new GraphicsLayer();
		mMapView.addLayer(resultGeomLayer);
		
		
		Options o = new Options();
		o.mode = MODE.ONDEMAND;
		o.outFields = new String[] { "FIELD_KID", "APPROXACRE", "FIELD_NAME",
				"STATUS", "PROD_GAS", "PROD_OIL", "ACTIVEPROD", "CUMM_OIL",
				"MAXOILWELL", "LASTOILPRO", "LASTOILWEL", "LASTODATE",
				"CUMM_GAS", "MAXGASWELL", "LASTGASPRO", "LASTGASWEL",
				"LASTGDATE", "AVGDEPTH", "AVGDEPTHSL", "FIELD_TYPE",
				"FIELD_KIDN" };
		fLayer = new ArcGISFeatureLayer("http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Petroleum/KSPetro/MapServer/1",o);
		SimpleFillSymbol fiedldsSelectionSymbol = new SimpleFillSymbol(Color.MAGENTA);
		fiedldsSelectionSymbol.setOutline(new SimpleLineSymbol(Color.YELLOW, 2));
		fLayer.setSelectionSymbol(fiedldsSelectionSymbol);		

		mMapView.addLayer(fLayer);
		
		
		
		// set Identify Parameters
		params = new IdentifyParameters();
		params.setTolerance(20);
		params.setDPI(98);
		params.setLayers(new int[] { 1 });
		params.setLayerMode(IdentifyParameters.ALL_LAYERS);
		
		Identifybool =false;
		mMapView.setOnSingleTapListener(new OnSingleTapListener() {

			private static final long serialVersionUID = 1L;

			public void onSingleTap(final float x, final float y) {

				if (!mMapView.isLoaded()) {
					return;
				}
				if(Identifybool){
				// Add to Identify Parameters based on tapped location
				Point identifyPoint = mMapView.toMapPoint(x, y);
				params.setGeometry(identifyPoint);
				SpatialReference sr = SpatialReference.create(4267);
				params.setSpatialReference(sr);
				params.setMapHeight(mMapView.getHeight());
				params.setMapWidth(mMapView.getWidth());
				// add the area of extent to identify parameters
				Envelope env = new Envelope();
				mMapView.getExtent().queryEnvelope(env);
				params.setMapExtent(env);
				// execute the identify task off UI thread
				MyIdentifyTask mTask = new MyIdentifyTask(identifyPoint);
				mTask.execute(params);
				}
				
				if(Bufferbool){
					
					try {
						singleTapAct(x, y);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
				}
			}

		});
		
		queryButton = (Button) findViewById(R.id.CopyExtent);
		queryButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				LinearLayout Lay = (LinearLayout) findViewById(R.id.ExtentView);
				Lay.setVisibility(View.INVISIBLE);
				
			}
		});
		
		
		queryButton2 = (Button) findViewById(R.id.BufferDone);
		queryButton2.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				LinearLayout Lay2 = (LinearLayout) findViewById(R.id.BufferView);
				Lay2.setVisibility(View.INVISIBLE);
				firstGeomLayer.removeAll();
				resultGeomLayer.removeAll();
				Bufferbool = false;
				
			}
		});
		//
		
		
		bufferDistance = (SeekBar) findViewById(R.id.distance);
		// set default progress
		bufferDistance.setMax(10000);
		bufferDistance.setProgress(3000);

		bufferDistTextValue = (TextView) findViewById(R.id.bufferdistance);

		bufferDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// Use progress as the distance and calculate the buffer
						// geometry again and plot it on the map
						bufferDist = progress;
						bufferDistTextValue.setText((Integer.toString(progress))
								+ "m");
						doBuffer();

					}
				});


		
		
	}
	enum GEOMETRY_TYPE {
		point, polyline, polygon
	}
	
	volatile int countTap = 0;
	Geometry firstGeometry = null;
	GEOMETRY_TYPE firstGeoType = GEOMETRY_TYPE.point;
	GraphicsLayer firstGeomLayer = null;
	GraphicsLayer resultGeomLayer = null;
	SpatialReference spatialRef = SpatialReference.create(4267);
	double bufferDist = 3000;
	
	void singleTapAct(float x, float y) throws Exception {
		countTap++;
		Point point = mMapView.toMapPoint(x, y);
		Log.d("sigle tap on screen:", "[" + x + "," + y + "]");
		Log.d("sigle tap on map:", "[" + point.getX() + "," + point.getY()
				+ "]");

		if (firstGeometry == null) {
			if (firstGeoType == GEOMETRY_TYPE.point) {
				firstGeometry = point;
				}
		}
		if (firstGeoType == null)
			return;
		int color1 = Color.BLUE;
		drawGeomOnGraphicLyr(firstGeometry, firstGeomLayer, point,
				firstGeoType, color1, false);
		Log.d("geometry step " + countTap, GeometryEngine.geometryToJson(
				mMapView.getSpatialReference(), firstGeometry));
		doBuffer();	
	
		
	}
	
	
	
	void drawGeomOnGraphicLyr(Geometry geometryToDraw, GraphicsLayer glayer,
			Point point, GEOMETRY_TYPE geoTypeToDraw, int color,
			boolean startPointSet) {

		if (geoTypeToDraw == GEOMETRY_TYPE.point) {
			geometryToDraw = point;

		} 

		Geometry[] geoms = new Geometry[1];
		geoms[0] = geometryToDraw;

		try {
			glayer.removeAll();
			GeometryUtil.highlightGeometriesWithColor(geoms, glayer, color);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	int uid= -1;
	Graphic gbuffer;
	public void doBuffer() {

		Geometry geom[] = { firstGeometry };

		resultGeomLayer.removeAll();

		if (firstGeometry != null) {
			try {

				Unit unit = spatialRef.getUnit();

				double adjustedAccuracy = bufferDist;

				if (unit.getUnitType() == UnitType.ANGULAR) {
					adjustedAccuracy = metersToDegrees(bufferDist);
				} else {
					unit = Unit.create(LinearUnit.Code.METER);
				}
				// get the result polygon from the buffer operation
				Polygon p = GeometryEngine.buffer(geom[0], spatialRef,
						adjustedAccuracy, unit);

				// Render the polygon on the result graphic layer
				SimpleFillSymbol sfs = new SimpleFillSymbol(Color.GREEN);
				sfs.setOutline(new SimpleLineSymbol(Color.RED, 4,
						com.esri.core.symbol.SimpleLineSymbol.STYLE.SOLID));
				sfs.setAlpha(25);
				Graphic g = new Graphic(p, sfs);
				uid = resultGeomLayer.addGraphic(g);
				
				
				/////////////
				
				if (uid != -1) {
					gbuffer = resultGeomLayer.getGraphic(uid);
					if (gbuffer!= null && gbuffer.getGeometry() != null) {
						fLayer.clearSelection();
						Query q = new Query();
						// optional
						q.setWhere("1=1");
						q.setReturnGeometry(true);
						q.setInSpatialReference(mMapView.getSpatialReference());
						q.setGeometry(gbuffer.getGeometry());
						q.setSpatialRelationship(SpatialRelationship.INTERSECTS);

						fLayer.selectFeatures(q, SELECTION_METHOD.NEW, callback);
					}
					//gLayer.removeAll();

				}

				//p0 = null;
				// Resets it
				uid = -1;
				
				
				////////////////
				

			} catch (Exception ex) {
				Log.d("Test buffer", ex.getMessage());

			}
			//enableSketching = false;
		}
		firstGeometry = null;
	}

	CallbackListener<FeatureSet> callback = new CallbackListener<FeatureSet>() {

		public void onCallback(FeatureSet fSet) {			

		}

		public void onError(Throwable arg0) {
			resultGeomLayer.removeAll();
		}
	};
	
	private final double metersToDegrees(double distanceInMeters) {
		return distanceInMeters / 111319.5;
	}
	
	
	private ViewGroup createIdentifyContent(final List<IdentifyResult> results) {

		// create a new LinearLayout in application context
		LinearLayout layout = new LinearLayout(this);
		// view height and widthwrap content
		layout.setLayoutParams(new LayoutParams(100,
				LayoutParams.WRAP_CONTENT));
		// default orientation
		layout.setOrientation(LinearLayout.HORIZONTAL);
		// Spinner to hold the results of an identify operation
		IdentifyResultSpinner spinner = new IdentifyResultSpinner(this,
				(List<IdentifyResult>) results);
		// make view clickable
		spinner.setClickable(true);
		// MyIdentifyAdapter creates a bridge between spinner and it's data
		MyIdentifyAdapter adapter = new MyIdentifyAdapter(this, results);
		spinner.setAdapter(adapter);
		spinner.setLayoutParams(new LayoutParams(100,
				LayoutParams.WRAP_CONTENT));
		layout.addView(spinner);

		return layout;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_GetExtent:
			//ExtentView
			
			LinearLayout Lay = (LinearLayout) findViewById(R.id.ExtentView);
			Lay.setVisibility(View.VISIBLE);
			
			String Extents = mMapView.getMapBoundaryExtent().toString();
			txt_Extent = (EditText) findViewById(R.id.txt_Extent);
			txt_Extent.setText(Extents);
			return true;
		case R.id.action_Clear:
			firstGeometry = null;
			graphicsLayer.removeAll();
			graphicsLayerpoint.removeAll();
			firstGeomLayer.removeAll();
			resultGeomLayer.removeAll();
			fLayer.removeAll();
			mMapView.getCallout().hide();
			Bufferbool = false;
			LinearLayout Lay3 = (LinearLayout) findViewById(R.id.BufferView);
			Lay3.setVisibility(View.INVISIBLE);
			
			Toast toast = Toast.makeText(MainActivity.this, "Clear Graphics",
					Toast.LENGTH_LONG);
			toast.show();
			
			
			return true;
		case R.id.SelectPolygon:
			
			polyGraphics = true;
		 
			String targetLayer = targetServerURL.concat("/1");
			String[] queryParams = { targetLayer, "FIELD_NAME = 'FOREST CITY COAL GAS AREA' " };
			AsyncQueryTask ayncQuery = new AsyncQueryTask();
			ayncQuery.execute(queryParams);
			
			return true;
		case R.id.SelectPoint:
			pointGraphics = true;
			String targetLayer2 = targetServerURL.concat("/0");
			String[] queryParams2 = { targetLayer2, "OBJECTID > 20  " };
			AsyncQueryTask ayncQuery2 = new AsyncQueryTask();
			ayncQuery2.execute(queryParams2);
			
			//Intent intent = new Intent(MainActivity.this ,displaydata.class);
			//this.startActivity(intent);
			
			
			return true;
			
		case R.id.action_Identify:
			
			Identifybool = true;
			Toast toast2 = Toast.makeText(MainActivity.this, "Tab on map to identify",Toast.LENGTH_LONG);
			toast2.show();
			
			return true;
		case R.id.Buffer:
			
			LinearLayout Lay6 = (LinearLayout) findViewById(R.id.BufferView);
			Lay6.setVisibility(View.VISIBLE);
			
			Toast toast3 = Toast.makeText(MainActivity.this, "Tab on map",Toast.LENGTH_LONG);
			toast3.show();
			Bufferbool = true;

			return true;
			
		case R.id.openpage:
			Intent intent = new Intent(MainActivity.this ,displaydata.class);
			this.startActivity(intent);
			return true;
		default:		
			return super.onOptionsItemSelected(item);
		}
	
	}
	
	
	private class AsyncQueryTask extends AsyncTask<String, Void, FeatureSet> {

		protected void onPreExecute() {
			progress = ProgressDialog.show(MainActivity.this, "",
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
					if(pointGraphics){
						
						graphicsLayerpoint.addGraphics(grs);
						//mMapView.zoom
						message = (grs.length == 1 ? "1 result has " : Integer
								.toString(grs.length) + " results have ")
								+ "come back";
						pointGraphics = false;
					}else if(polyGraphics){
					
						graphicsLayer.addGraphics(grs);
						message = (grs.length == 1 ? "1 result has " : Integer
							.toString(grs.length) + " results have ")
							+ "come back";
						polyGraphics = false;
					}
				}

			}
			progress.dismiss();

			Toast toast = Toast.makeText(MainActivity.this, message,
					Toast.LENGTH_LONG);
			toast.show();
			//queryButton.setText("Clear graphics");
			//boolQuery = false;

		}

	}
	
	private class MyIdentifyTask extends AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {

		IdentifyTask mIdentifyTask;
		Point mAnchor;

		MyIdentifyTask(Point anchorPoint) {
			mAnchor = anchorPoint;
		}

		@Override
		protected void onPreExecute() {
			// create dialog while working off UI thread
			dialog = ProgressDialog.show(MainActivity.this, "Identify Task",
					"Identify query ...");
			//
			mIdentifyTask = new IdentifyTask(targetServerURL);
		}

		@Override
		protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
			IdentifyResult[] mResult = null;
			// check that you have the identify parameters
			if (params != null && params.length > 0) {
				IdentifyParameters mParams = params[0];
				try {
					// Run IdentifyTask with Identify Parameters
					mResult = mIdentifyTask.execute(mParams);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return mResult;
		}

		@Override
		protected void onPostExecute(IdentifyResult[] results) {

			// dismiss dialog
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			ArrayList<IdentifyResult> resultList = new ArrayList<IdentifyResult>();
			for (int index = 0; index < results.length; index++) {

				if (results[index].getAttributes().get(
						results[index].getDisplayFieldName()) != null)
					resultList.add(results[index]);
			}

			mMapView.getCallout().show(mAnchor,createIdentifyContent(resultList));
		}



	}
	
	public class MyIdentifyAdapter extends IdentifyResultSpinnerAdapter {
		String m_show = null;
		List<IdentifyResult> resultList;
		int currentDataViewed = -1;
		Context m_context;

		public MyIdentifyAdapter(Context context, List<IdentifyResult> results) {
			super(context, results);
			this.resultList = results;
			this.m_context = context;

		}

		// Get a TextView that displays identify results in the callout.
		public View getView(int position, View convertView, ViewGroup parent) {
			String outputVal = null;

			// Get Name attribute from identify results
			IdentifyResult curResult = this.resultList.get(position);
			if (curResult.getAttributes().containsKey("Field Name")) {
				outputVal = curResult.getAttributes().get("Field Name").toString();
			}else{
				
				outputVal = "No Result";
			}

			// Create a TextView to write identify results
			TextView txtView;
			txtView = new TextView(this.m_context);
			txtView.setText(outputVal);
			txtView.setTextColor(Color.BLACK);
			txtView.setLayoutParams(new ListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			txtView.setGravity(Gravity.CENTER_VERTICAL);
			Identifybool =false;
			return txtView;
		}

	}


}
