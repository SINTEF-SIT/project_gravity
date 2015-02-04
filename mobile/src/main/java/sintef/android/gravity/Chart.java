package sintef.android.gravity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import de.greenrobot.event.EventBus;
import sintef.android.controller.sensor.SensorData;

/**
 * Created by samyboy89 on 29/01/15.
 */
public class Chart implements View.OnClickListener {

    private final Activity mActivity;

    private static Random RAND = new Random();
    private static final float RATIO = 0.618033988749895f;

    private static final String TIME = "H:mm:ss";

    private static final int TEN_SEC = 10000;
    private static final int TWO_SEC = 2000;

    private View mViewZoomIn;
    private View mViewZoomOut;
    private View mViewZoomReset;
    private GraphicalView mChartView;
    private XYMultipleSeriesRenderer mRenderer;
    private XYMultipleSeriesDataset mDataset;
    private HashMap<String, TimeSeries> mSeries;
    private double mYAxisMin = Double.MAX_VALUE;
    private double mYAxisMax = Double.MIN_VALUE;
    private double mZoomLevel = 1;
    private int mYAxisPadding = 10;

    private final ZoomListener mZoomListener = new ZoomListener() {
        @Override
        public void zoomReset() {
            mZoomLevel = 1;
            scrollGraph(new Date().getTime());
        }

        @Override
        public void zoomApplied(final ZoomEvent event) {
            if (event.isZoomIn()) {
                mZoomLevel /= 2;
            }
            else {
                mZoomLevel *= 2;
            }
            scrollGraph(new Date().getTime());
        }
    };

    public Chart(Activity activity, ViewGroup chartView) {
        mActivity = activity;
        EventBus.getDefault().registerSticky(this);

        mSeries = new HashMap<>();
        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();

        mRenderer.setLabelsColor(Color.LTGRAY);
        mRenderer.setAxesColor(Color.LTGRAY);
        mRenderer.setGridColor(Color.rgb(136, 136, 136));
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setApplyBackgroundColor(true);

        mRenderer.setLegendTextSize(20);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setPointSize(8);
        mRenderer.setMargins(new int[] { 60, 60, 60, 60 });

        mRenderer.setFitLegend(true);
        mRenderer.setShowGrid(true);
        mRenderer.setZoomEnabled(true);
        mRenderer.setExternalZoomEnabled(true);
        mRenderer.setAntialiasing(true);
        mRenderer.setInScroll(true);

        // onCreateView
        if (Configuration.ORIENTATION_PORTRAIT == mActivity.getResources().getConfiguration().orientation) {
            mYAxisPadding = 9;
            mRenderer.setYLabels(15);
        }

        mChartView = ChartFactory.getTimeChartView(mActivity, mDataset, mRenderer, TIME);
        mChartView.addZoomListener(mZoomListener, true, false);
        chartView.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mViewZoomIn = mActivity.findViewById(R.id.zoom_in);
        mViewZoomOut = mActivity.findViewById(R.id.zoom_out);
        mViewZoomReset = mActivity.findViewById(R.id.zoom_reset);
        mViewZoomIn.setOnClickListener(this);
        mViewZoomOut.setOnClickListener(this);
        mViewZoomReset.setOnClickListener(this);
    }

    public void onEvent(SensorData data) {
        if (mSeries.containsKey(data.getSensorSession().getId())) {
            TimeSeries series = mSeries.get(data.getSensorSession().getId());
            series.add(data.getTimeCaptured(), (float) data.getSensorData());
        } else {
            TimeSeries series = new TimeSeries(data.getSensorSession().getId());
            series.add(data.getTimeCaptured(), (float) data.getSensorData());
            mSeries.put(data.getSensorSession().getId(), series);
            mDataset.addSeries(series);
            mRenderer.addSeriesRenderer(getSeriesRenderer(randomColor()));
        }
        scrollGraph(data.getTimeCaptured());

        mChartView.repaint();
    }

    private void scrollGraph(final long time) {
        final double[] limits = new double[] { time - TEN_SEC * mZoomLevel, time + TWO_SEC * mZoomLevel, mYAxisMin - mYAxisPadding,
                mYAxisMax + mYAxisPadding };
        mRenderer.setRange(limits);
    }

    private XYSeriesRenderer getSeriesRenderer(final int color) {
        final XYSeriesRenderer r = new XYSeriesRenderer();
        r.setDisplayChartValues(false);
        r.setPointStrokeWidth(0);
        r.setColor(color);
        r.setFillPoints(false);
        r.setLineWidth(4);
        return r;
    }


    private static int randomColor() {
        final float hue = (RAND.nextInt(360) + RATIO);
        return Color.HSVToColor(new float[] { hue, 0.8f, 0.9f });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.zoom_in:
                mChartView.zoomIn();
                break;

            case R.id.zoom_out:
                mChartView.zoomOut();
                break;

            case R.id.zoom_reset:
                mChartView.zoomReset();
                break;

            default:
                break;
        }

    }
}
