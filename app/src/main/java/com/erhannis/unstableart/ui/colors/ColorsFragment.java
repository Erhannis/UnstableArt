package com.erhannis.unstableart.ui.colors;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.erhannis.unstableart.R;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.IntColor;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

/**
 * //TODO Add support for high-bit colors
 * //TODO Add support for picking colors by uuid
 * //TODO Add support for picking colors by formula over uuids?
 */
public class ColorsFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  protected Color mCurColor;

  private LinearLayout llView;

  private OnColorsFragmentInteractionListener mListener;

  public ColorsFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment ColorsFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ColorsFragment newInstance(String param1, String param2) {
    ColorsFragment fragment = new ColorsFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
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
      colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
        @Override
        public void onColorChanged(int newColor) {
          //TODO Support DoublesColor etc.
          mCurColor = new IntColor(newColor);
        }
      });

      Button btnSelectColor = (Button)llView.findViewById(R.id.btnSelectColor);
      btnSelectColor.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mCurColor != null) {
            selectColor(mCurColor);
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
          mListener.onSelectColor(color);
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnColorsFragmentInteractionListener) {
      mListener = (OnColorsFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnColorsFragmentInteractionListener");
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
