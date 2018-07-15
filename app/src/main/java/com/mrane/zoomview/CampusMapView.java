package com.mrane.zoomview;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import app.insti.R.drawable;
import app.insti.fragment.MapFragment;

import com.mrane.campusmap.SettingsManager;
import com.mrane.data.Building;
import com.mrane.data.Marker;
import com.mrane.data.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CampusMapView extends SubsamplingScaleImageView {
	private MapFragment mainActivity;
	private HashMap<String, Marker> data;
	private Collection<Marker> markerList;
	private ArrayList<Marker> addedMarkerList;
	private ArrayList<Marker> specialMarkerList;
	private ArrayList<Marker> convoMarkerList;
	private Marker resultMarker;
	private Bitmap bluePointer;
	private Bitmap yellowPointer;
	private Bitmap greenPointer;
	private Bitmap grayPointer;
	private Bitmap blueMarker;
	private Bitmap yellowMarker;
	private Bitmap greenMarker;
	private Bitmap grayMarker;
	private Bitmap blueLockedMarker;
	private Bitmap blueConvoMarker;
	private Bitmap yellowLockedMarker;
	private Bitmap greenLockedMarker;
	private Bitmap grayLockedMarker;
	private float pointerWidth = 12;
	private float highlightedMarkerScale;
	private Paint paint;
	private Paint textPaint;
	private Paint strokePaint;
	private Rect bounds = new Rect();
	private static int RATIO_SHOW_PIN = 10;
	private static int RATIO_SHOW_PIN_TEXT = 20;
	private static long DURATION_MARKER_ANIMATION = 500;
	private static long DELAY_MARKER_ANIMATION = 675;
	private static float MAX_SCALE = 1F;
	private DisplayMetrics displayMetrics;
	private float density;
	private boolean isFirstLoad = true;
	private SettingsManager settingsManager;

	public CampusMapView(Context context) {
		this(context, null);
	}

	public CampusMapView(Context context, AttributeSet attr) {
		super(context, attr);
		initialise();
	}

	private void initialise() {
		displayMetrics = getResources().getDisplayMetrics();
		density = displayMetrics.density;
		highlightedMarkerScale = 1.0f;
		initMarkers();

		initPaints();

		mainActivity = MapFragment.getMainActivity();
		
		setGestureDetector();
		super.setMaxScale(density * MAX_SCALE);
	}

	@Override
	protected void onImageReady() {
		if (isFirstLoad) {
			Runnable runnable = new Runnable() {
				public void run() {
					AnimationBuilder anim;
						anim = animateScaleAndCenter(
								getTargetMinScale(), MapFragment.MAP_CENTER);
					anim.withDuration(MapFragment.DURATION_INIT_MAP_ANIM)
							.start();
					isFirstLoad = false;
				}
			};
			mainActivity.getActivity().runOnUiThread(runnable);
		}
	}
	
	public void setSettingsManager(SettingsManager sm){
		settingsManager = sm;
	}

	public void setFirstLoad(boolean b) {
		isFirstLoad = b;
	}

	private void initMarkers() {
		float w = 0;
		float h = 0;
		Options options = new BitmapFactory.Options();
		options.inScaled = true;

		bluePointer = BitmapFactory.decodeResource(getResources(),
				drawable.marker_dot_blue, options);
		blueMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_blue_s, options);
		blueLockedMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_blue_h, options);
		blueConvoMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_blue_h_convo, options);

		yellowPointer = BitmapFactory.decodeResource(getResources(),
				drawable.marker_dot_yellow, options);
		yellowMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_yellow_s, options);
		yellowLockedMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_yellow_h, options);

		greenPointer = BitmapFactory.decodeResource(getResources(),
				drawable.marker_dot_green, options);
		greenMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_green_s, options);
		greenLockedMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_green_h, options);

		grayPointer = BitmapFactory.decodeResource(getResources(),
				drawable.marker_dot_gray, options);
		grayMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_gray_s, options);
		grayLockedMarker = BitmapFactory.decodeResource(getResources(),
				drawable.marker_gray_h, options);
		w = pointerWidth*density;
		h = bluePointer.getScaledHeight(displayMetrics) * (w / bluePointer.getScaledWidth(displayMetrics));

		bluePointer = Bitmap.createScaledBitmap(bluePointer, (int) w, (int) h,
				true);
		bluePointer = Bitmap.createScaledBitmap(bluePointer, (int) w, (int) h,
				true);
		yellowPointer = Bitmap.createScaledBitmap(yellowPointer, (int) w,
				(int) h, true);
		greenPointer = Bitmap.createScaledBitmap(greenPointer, (int) w,
				(int) h, true);
		grayPointer = Bitmap.createScaledBitmap(grayPointer, (int) w, (int) h,
				true);
		w = 4f * w;
		h = blueMarker.getScaledHeight(displayMetrics) * (w / blueMarker.getScaledWidth(displayMetrics));
		blueMarker = Bitmap.createScaledBitmap(blueMarker, (int) w, (int) h,
				true);
		yellowMarker = Bitmap.createScaledBitmap(yellowMarker, (int) w,
				(int) h, true);
		greenMarker = Bitmap.createScaledBitmap(greenMarker, (int) w, (int) h,
				true);
		grayMarker = Bitmap.createScaledBitmap(grayMarker, (int) w, (int) h,
				true);
		blueLockedMarker = Bitmap.createScaledBitmap(blueLockedMarker, (int) w,
				(int) h, true);
		blueConvoMarker = Bitmap.createScaledBitmap(blueConvoMarker, (int) w,
				(int) h, true);
		yellowLockedMarker = Bitmap.createScaledBitmap(yellowLockedMarker,
				(int) w, (int) h, true);
		greenLockedMarker = Bitmap.createScaledBitmap(greenLockedMarker,
				(int) w, (int) h, true);
		grayLockedMarker = Bitmap.createScaledBitmap(grayLockedMarker, (int) w,
				(int) h, true);
	}

	private void initPaints() {
		paint = new Paint();
		paint.setAntiAlias(true);
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		// textPaint.setColor(Color.rgb(254, 250, 217));
		textPaint.setColor(Color.WHITE);
		textPaint.setShadowLayer(8.0f * density, -1 * density, 1 * density,
				Color.BLACK);
		textPaint.setTextSize(16 * density);
		Typeface boldCn = Typeface.createFromAsset(getContext().getAssets(),
				MapFragment.FONT_SEMIBOLD);
		textPaint.setTypeface(boldCn);

		strokePaint = new Paint();
		strokePaint.setAntiAlias(true);
		strokePaint.setColor(Color.BLACK);
		strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
		strokePaint.setTextSize(14 * density);
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setStrokeJoin(Join.ROUND);
		strokePaint.setStrokeWidth(0.2f * density);

	}

	public float getTargetMinScale() {
		return Math.max(getWidth() / (float) getSWidth(), (getHeight())
				/ (float) getSHeight());
	}

	public void setData(HashMap<String, Marker> markerData) {
		data = markerData;
		markerList = data.values();
		addedMarkerList = new ArrayList<Marker>();
		specialMarkerList = new ArrayList<Marker>();
		convoMarkerList = new ArrayList<Marker>();
		setSpecialMarkers();
	}

	private void setSpecialMarkers() {
		for (Marker m : markerList) {
			if (m.isShowDefault()) {
				specialMarkerList.add(m);
			}
		}
	}

	public static int getShowPinRatio() {
		return RATIO_SHOW_PIN;
	}

	public static void setShowPinRatio(int ratio) {
		RATIO_SHOW_PIN = ratio;
	}

	public static int getShowPinTextRatio() {
		return RATIO_SHOW_PIN_TEXT;
	}

	public static void setShowPinTextRatio(int ratio) {
		RATIO_SHOW_PIN_TEXT = ratio;
	}

	public Marker getResultMarker() {
		return resultMarker;
	}

	@Deprecated
	public Marker getHighlightedMarker() {
		return getResultMarker();
	}

	public void setResultMarker(Marker marker) {
		resultMarker = marker;
	}

	public boolean isResultMarker(Marker marker) {
		if (resultMarker == null)
			return false;
		return resultMarker == marker;
	}

	public void showResultMarker() {
		if (resultMarker != null) {
			boolean noDelay = false;
			if (isInView(getResultMarker().getPoint()))
				noDelay = true;
			AnimationBuilder anim = animateScaleAndCenter(getShowTextScale(),
					resultMarker.getPoint());
			anim.withDuration(750).start();
			setMarkerAnimation(noDelay, MapFragment.SOUND_ID_RESULT);
		}
	}

	public void setAndShowResultMarker(Marker marker) {
		setResultMarker(marker);
		showResultMarker();
	}

	public void addMarker(Marker m) {
		if (!addedMarkerList.contains(m)) {
			addedMarkerList.add(m);

		}
	}

	public void addMarker() {
		Marker m = getResultMarker();
		addMarker(m);
	}

	public void addMarkers(Collection<? extends Marker> markers) {
		for (Marker m : markers) {
			addMarker(m);
		}
	}

	public void addMarkers(Marker[] markerArray) {
		List<Marker> markerList = Arrays.asList(markerArray);
		addMarkers(markerList);
	}

	public void removeAddedMarker(Marker m) {
		if (addedMarkerList.contains(m)) {
			addedMarkerList.remove(m);
		}
	}

	public void removeAddedMarkers(Collection<? extends Marker> markers) {
		for (Marker m : markers) {
			removeAddedMarker(m);
		}
	}

	public void removeAddedMarkers(Marker[] markerArray) {
		List<Marker> markerList = Arrays.asList(markerArray);
		removeAddedMarkers(markerList);
	}

	public void removeAddedMarkers() {
		addedMarkerList.clear();
	}

	public boolean isAddedMarker(Marker m) {
		return addedMarkerList.contains(m);
	}

	public boolean isAddedMarker() {
		return isAddedMarker(getResultMarker());
	}

	public void toggleMarker(Marker m) {
		if (isAddedMarker(m)) {
			removeAddedMarker(m);
			mainActivity.playAnimSound(MapFragment.SOUND_ID_REMOVE);
		} else {
			addMarker(m);
			if (!isInView(m.getPoint())) {
				AnimationBuilder anim = animateScaleAndCenter(
						getShowTextScale(), m.getPoint());
				anim.withDuration(750).start();
				setMarkerAnimation(false, MapFragment.SOUND_ID_ADD);
			} else {
				setMarkerAnimation(true, MapFragment.SOUND_ID_ADD);
			}
		}
		invalidate();
	}

	public void toggleMarker() {
		toggleMarker(getResultMarker());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Don't draw pin before image is ready so it doesn't move around during
		// setup.
		if (!isImageReady()) {
			return;
		}

		for (Marker marker : markerList) {
			if (isInView(marker.getPoint())) {
				if (isShowPinScale(marker)
						&& !(isResultMarker(marker) || addedMarkerList
								.contains(marker))) {
					if (shouldShowUp(marker))
						drawPionterAndText(canvas, marker);
				}
			}
		}
		for (Marker marker : addedMarkerList) {
			if (isInView(marker.getPoint())) {
				if (!isResultMarker(marker)) {
					drawMarkerBitmap(canvas, marker);
					drawMarkerText(canvas, marker);
				}
			}
		}
		Marker marker = getResultMarker();
		if (marker != null) {
			if (isInView(marker.getPoint())) {
				drawMarkerBitmap(canvas, marker);
				drawMarkerText(canvas, marker);
			}
		}

	}

	private boolean shouldShowUp(Marker marker) {
		boolean result = true;
		if(marker.getGroupIndex() == Marker.RESIDENCES){
			result = settingsManager.showResidences();
		}
		if (marker instanceof Building) {
			String[] childKeys = ((Building) marker).children;
			for (String childKey : childKeys) {
				Marker child = data.get(childKey);
				if (isAddedMarker(child) || isResultMarker(child)) {
					result = false;
					break;
				}
			}
		}
		if (marker instanceof Room)
			result = false;
		return result;
	}

	private void drawMarkerBitmap(Canvas canvas, Marker marker) {
		Bitmap highlightedPin = getMarkerBitmap(marker);
		PointF vPin = sourceToViewCoord(marker.getPoint());
		float vX = vPin.x - (highlightedPin.getWidth() / 2);
		float vY = vPin.y - highlightedPin.getHeight();
		canvas.drawBitmap(highlightedPin, vX, vY, paint);
	}

	private void drawMarkerText(Canvas canvas, Marker marker) {
		String name;
		PointF vPin = sourceToViewCoord(marker.getPoint());
		if (marker.getShortName().equals("0") || marker.getShortName().isEmpty())
			name = marker.getName();
		else
			name = marker.getShortName();
		textPaint.getTextBounds(name, 0, name.length() - 1, bounds);
		float tX = vPin.x - bounds.width() / 2;
		float tY = vPin.y + bounds.height();
		canvas.drawText(name, tX, tY, textPaint);
		// canvas.drawText(names[0], tX, tY, strokePaint);
	}

	private void drawPionterAndText(Canvas canvas, Marker marker) {
		Bitmap pin = getPointerBitmap(marker);
		PointF vPin = sourceToViewCoord(marker.getPoint());
		float vX = vPin.x - (pin.getWidth() / 2);
		float vY = vPin.y - (pin.getHeight() / 2);
		canvas.drawBitmap(pin, vX, vY, paint);
		if (isShowPinTextScale(marker)) {
			String name;
			if (marker.getShortName().equals("0") || marker.getShortName().isEmpty())
				name = marker.getName();
			else
				name = marker.getShortName();
			Paint temp = new Paint(textPaint);
			if(marker.getGroupIndex() == Marker.RESIDENCES) temp.setTextSize(12*density);
			textPaint.getTextBounds(name, 0, name.length() - 1, bounds);
			float tX = vPin.x + pin.getWidth();
			float tY = vPin.y + bounds.height() / 2;
			canvas.drawText(name, tX, tY, temp);
		}
	}

	private boolean isInView(PointF point) {
		int displayWidth = displayMetrics.widthPixels;
		int displayHeight = displayMetrics.heightPixels;

		int viewX = (int) sourceToViewCoord(point).x;
		int viewY = (int) sourceToViewCoord(point).y;

		if (viewX > -displayWidth / 3 && viewX < displayWidth && viewY > 0
				&& viewY < displayHeight)
			return true;

		return false;
	}

	private Marker getNearestMarker(PointF touchPoint) {
		Marker resultMarker = null;
		float minDist = 100000000f;
		for (Marker marker : markerList) {
			PointF point = marker.getPoint();
			float dist = (float) calculateDistance(point, touchPoint);

			if (dist < minDist && isMarkerVisible(marker)) {
				minDist = dist;
				resultMarker = marker;
			}
		}
		return resultMarker;
	}

	private double calculateDistance(PointF point1, PointF point2) {
		float xDiff = point1.x - point2.x;
		float yDiff = point1.y - point2.y;

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

	private Bitmap getPointerBitmap(Marker marker) {
		int color = marker.getColor();

		if (color == Marker.COLOR_BLUE) {
			return bluePointer;
		} else if (color == Marker.COLOR_YELLOW) {
			return yellowPointer;
		} else if (color == Marker.COLOR_GREEN) {
			return greenPointer;
		} else if (color == Marker.COLOR_GRAY) {
			return grayPointer;
		}

		return bluePointer;
	}

	private Bitmap getMarkerBitmap(Marker marker) {
		int color = marker.getColor();

		Bitmap markerBitmap = null;

		if (color == Marker.COLOR_BLUE) {
			markerBitmap = blueMarker;
			if (isAddedMarker(marker)){
				markerBitmap = blueLockedMarker;
				if(convoMarkerList.contains(marker)) markerBitmap = blueConvoMarker;
			}
		} else if (color == Marker.COLOR_YELLOW) {
			markerBitmap = yellowMarker;
			if (isAddedMarker(marker))
				markerBitmap = yellowLockedMarker;
		} else if (color == Marker.COLOR_GREEN) {
			markerBitmap = greenMarker;
			if (isAddedMarker(marker))
				markerBitmap = greenLockedMarker;
		} else if (color == Marker.COLOR_GRAY) {
			markerBitmap = grayMarker;
			if (isAddedMarker(marker))
				markerBitmap = grayLockedMarker;
		}

		if (highlightedMarkerScale != 1.0f && isResultMarker(marker)) {
			float w = markerBitmap.getWidth() * highlightedMarkerScale;
			float h = markerBitmap.getHeight() * highlightedMarkerScale;
			markerBitmap = Bitmap.createScaledBitmap(markerBitmap, (int) w,
					(int) h, true);
		}

		if (isResultMarker(marker)) {
			float w = markerBitmap.getWidth() * 1.2f;
			float h = markerBitmap.getHeight() * 1.2f;
			markerBitmap = Bitmap.createScaledBitmap(markerBitmap, (int) w,
					(int) h, true);
		}

		return markerBitmap;
	}

	private void setMarkerAnimation(boolean noDelay, int _sound_index) {
		final int sound_index = _sound_index;
		long delay = 0;
		if (!noDelay) {
			delay = DELAY_MARKER_ANIMATION;
		}

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			playAnim(delay);
		} else {
			highlightedMarkerScale = 1.0f;
		}
		mainActivity.playAnimSoundDelayed(sound_index, delay);
		if (isImageReady())
			invalidate();
	}

	@SuppressLint("NewApi")
	private void playAnim(long delay) {
		highlightedMarkerScale = 0.1f;
		ValueAnimator valAnim = new ValueAnimator();
		valAnim.setFloatValues(0.1f, 1.0f);
		valAnim.setDuration(DURATION_MARKER_ANIMATION);
		valAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				highlightedMarkerScale = (Float) animation.getAnimatedValue();
				if (isImageReady())
					invalidate();
			}
		});
		TimeInterpolator i = new BounceInterpolator();
		valAnim.setInterpolator(i);
		valAnim.setStartDelay(delay);
		valAnim.start();
	}
	
	public Runnable getScaleAnim(final float scale){
		Runnable anim = new Runnable() {
			public void run() {
				AnimationBuilder animation = animateScale(scale);
				animation
						.withDuration(200)
						.withEasing(
								SubsamplingScaleImageView.EASE_OUT_QUAD)
						.start();
			}
		};
		return anim;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(getTargetMinScale() > getScale()){
			setScaleAndCenter(getTargetMinScale(), getCenter());
		}
		super.onSizeChanged(w, h, oldw, oldh);
		
	}

	private void setGestureDetector() {
		final GestureDetector gestureDetector = new GestureDetector(
				mainActivity.getContext(), new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						if (isImageReady()) {
							PointF sCoord = viewToSourceCoord(e.getX(),
									e.getY());
							Marker marker = getNearestMarker(sCoord);
							if (isMarkerInTouchRegion(marker, sCoord)) {
								// mMainActivity.resultMarker(marker.name);
								mainActivity.editText.setText(marker.getName());
								mainActivity.displayMap();
							}
						} else {

						}
						return true;
					}
				});

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				final float targetMinScale = getTargetMinScale();
				int action = motionEvent.getAction();
				if(action== MotionEvent.ACTION_DOWN){
					if(motionEvent.getX()<20*density){
						getParent().requestDisallowInterceptTouchEvent(false);
						return true;
					}
					else{
//						CampusMapView.this.setPanEnabled(true);
					}
				}
				else if(action == MotionEvent.ACTION_UP){
					CampusMapView.this.setPanEnabled(true);
				}
				if (targetMinScale > getScale()) {
					callSuperOnTouch(motionEvent);
					
					if (action == MotionEvent.ACTION_UP) {
						
						Runnable anim = getScaleAnim(targetMinScale);
						if(isImageReady())	anim.run();
					}
					return true;
				}
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});

	}

	private void callSuperOnTouch(MotionEvent me) {
		super.onTouchEvent(me);
	}

	private boolean isMarkerInTouchRegion(Marker marker, PointF o) {
		if (marker != null) {
			PointF point = sourceToViewCoord(marker.getPoint());
			PointF origin = sourceToViewCoord(o);
			float dist = (float) calculateDistance(point, origin);
			if (dist < pointerWidth * density * 2 && isMarkerVisible(marker)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMarkerVisible(Marker marker) {
		if (marker == resultMarker)
			return true;
		if (addedMarkerList.contains(marker))
			return true;
		if (isShowPinScale(marker) && shouldShowUp(marker))
			return true;
		return false;
	}

	private boolean isShowPinScale(Marker m) {
		if (specialMarkerList.contains(m))
			return true;
		PointF left = viewToSourceCoord(0, 0);
		PointF right = viewToSourceCoord(getWidth(), 0);
		float xDpi = displayMetrics.xdpi;
		if ((right.x - left.x) * xDpi / getWidth() < getSWidth()
				/ RATIO_SHOW_PIN)
			return true;
		return false;
	}

	private boolean isShowPinTextScale(Marker m) {
		if (specialMarkerList.contains(m))
			return true;
		// PointF left = viewToSourceCoord(0, 0);
		// PointF right = viewToSourceCoord(getWidth(), 0);
		// float xDpi = displayMetrics.xdpi;
		// if((right.x-left.x)*xDpi/getWidth() <
		// getSWidth()*density/(RATIO_SHOW_PIN_TEXT*2)) return true;
		if (getScale() >= (getShowTextScale()))
			return true;
		return false;
	}

	private float getShowTextScale() {
		float xDpi = displayMetrics.xdpi;
		float scale = (RATIO_SHOW_PIN_TEXT * xDpi * 2 / density + 20)
				/ getSWidth();
		if (scale > getMaxScale()) {
			scale = 0.7f * getMaxScale();
		}
		return scale;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MapFragment.getMainActivity().setFollowingUser(false);
		return super.onTouchEvent(event);
	}

}
