package com.erhannis.unstableart.ui.tools;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.erhannis.unstableart.R;

//TODO Distinguish between meta-actions and art actions?
public class ActionsFragment extends Fragment {
  public interface OnActionsFragmentInteractionListener {
    public void onSelectAction(String action);
  }

  public static final String M_REDO = "Redo";
  public static final String M_UNDO = "Undo";
  public static final String M_COLOR = "Color";
  public static final String M_SIZE = "Size";
  public static final String M_CANVAS_MODE = "Toggle canvas mode";
  public static final String M_SAVE = "Save";
  public static final String M_SAVE_AS = "Save as...";
  public static final String M_LOAD = "Load...";
  public static final String M_EXPORT = "Export...";
  public static final String[] ACTIONS_MENU = {M_REDO, M_UNDO, M_COLOR, M_SIZE, M_CANVAS_MODE, M_SAVE, M_SAVE_AS, M_LOAD, M_EXPORT};

  private LinearLayout llView;

  private OnActionsFragmentInteractionListener mListener;

  public ActionsFragment() {
    // Required empty public constructor
  }

  public static ActionsFragment newInstance() {
    ActionsFragment fragment = new ActionsFragment();
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
    llView = (LinearLayout)inflater.inflate(R.layout.fragment_actions, container, false);

    ListView lvActions = (ListView)llView.findViewById(R.id.lvActions);

    lvActions.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ACTIONS_MENU));
    //TODO Set selected listener

    lvActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO Check if already selected?  Can that happen?
        String s = adapterView.getItemAtPosition(i).toString();
        selectAction(s);
      }
    });

    return llView;
  }

  protected void selectAction(String action) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          mListener.onSelectAction(action);
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnActionsFragmentInteractionListener) {
      mListener = (OnActionsFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnActionsFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  //TODO Extract somehow?
  public void showToast(String text) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ActionsFragment.this.getContext(), text, Toast.LENGTH_LONG).show();
      }
    });
  }
}
