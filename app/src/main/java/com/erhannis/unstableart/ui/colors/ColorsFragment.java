package com.erhannis.unstableart.ui.colors;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.erhannis.android.distributedui.ButtonsFragment;
import com.erhannis.android.distributedui.DistributedUiActivity;
import com.erhannis.mathnstuff.splines.BezierSpline;
import com.erhannis.mathnstuff.splines.ColorSolid;
import com.erhannis.mathnstuff.splines.ColorSpline;
import com.erhannis.unstableart.R;
import com.erhannis.unstableart.UAApplication;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.IntColor;
import com.erhannis.unstableart.mechanics.color.SplineColor;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * //TODO Add support for high-bit colors
 * //TODO Add support for picking colors by uuid
 * //TODO Add support for picking colors by formula over uuids?
 */
public class ColorsFragment extends Fragment {
  protected Color mCurColor;
  protected ColorSpline mSpline; //TODO Other kinds of spline?

  private LinearLayout llView;

  private DistributedUiActivity mListener;

  public ColorsFragment() {
    // Required empty public constructor
  }

  public static ColorsFragment newInstance() {
    ColorsFragment fragment = new ColorsFragment();
    Bundle args = new Bundle();
    //args.putString(ARG_PARAM1, param1);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      //mParam1 = getArguments().getString(ARG_PARAM1);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    llView = (LinearLayout)inflater.inflate(R.layout.fragment_colors, container, false);
    updateView();
    return llView;
  }

  public void updateView() {
    if (llView != null) {
      ColorPickerView colorPickerView = (ColorPickerView)llView.findViewById(R.id.nilssonpicker);
      colorPickerView.setAlphaSliderVisible(true);
      colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
        @Override
        public void onColorChanged(int newColor) {
          //TODO Support DoublesColor etc.
          mCurColor = new IntColor(newColor);
          selectColor(mCurColor);
        }
      });

      EditText etSplinePoints = (EditText)llView.findViewById(R.id.etSplinePoints);
      Button btnAddColorToSpline = (Button)llView.findViewById(R.id.btnAddColorToSpline);
      Button btnReticulateSpline = (Button)llView.findViewById(R.id.btnReticulateSpline);
      SeekBar sbSplineValue = (SeekBar)llView.findViewById(R.id.sbSplineValue);
      btnAddColorToSpline.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            double[] rgb = new double[] {mCurColor.getR(), mCurColor.getG(), mCurColor.getB()};
            Editable str = etSplinePoints.getText();
            str.insert(str.length()-1, ","+new Gson().toJson(rgb));
            //TODO Broadcast?
          }
        }
      });
      btnReticulateSpline.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            ColorSpline cs = new ColorSpline();
            cs.spline.SetOrder(2);
            cs.space = ColorSolid.RGB_CUBE;
            double[][] points = new Gson().fromJson(etSplinePoints.getText().toString(), double[][].class);
            double[][] ptsDimsFirst = new double[points[0].length][points.length];
            for (int i = 0; i < points.length; i++) {
              for (int j = 0; j < points[i].length; j++) {
                ptsDimsFirst[j][i] = points[i][j];
              }
            }
            try {
              cs.SetPoints(ptsDimsFirst);
            } catch (IllegalArgumentException e) {
              e.printStackTrace();
              Toast.makeText(UAApplication.getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
              return;
            }
            mSpline = cs;

            mCurColor = new SplineColor(mSpline, ((double)sbSplineValue.getProgress())/sbSplineValue.getMax());
            selectColor(mCurColor);
          }
        }
      });
      sbSplineValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
          mCurColor = new SplineColor(mSpline, ((double)seekBar.getProgress())/seekBar.getMax());
          selectColor(mCurColor);
        }
      });

      Button btnMoveFragment = (Button)llView.findViewById(R.id.btnMoveFragment);
      btnMoveFragment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            mListener.sendToHub("onMoveColorsFragment");
          }
        }
      });
    }
  }

  protected void selectColor(Color color) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          mListener.sendToHub("onSelectColor", color);
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof DistributedUiActivity && ((DistributedUiActivity)context).implementsInterface(OnColorsFragmentInteractionListener.class)) {
      mListener = (DistributedUiActivity) context;
    } else {
      throw new RuntimeException(context.toString() + " must report implementation OnColorsFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnColorsFragmentInteractionListener {
    public void onSelectColor(Color color);
    //TODO This is a hack
    public void onMoveColorsFragment();
  }

  public void showToast(String text) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ColorsFragment.this.getContext(), text, Toast.LENGTH_LONG).show();
      }
    });
  }
}
