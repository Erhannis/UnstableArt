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

//TODO Allow custom tools
public class ToolsFragment extends Fragment {
  public interface OnToolsFragmentInteractionListener {
    public void onSelectTool(String tool);
  }

  public static final String M_PEN = "Pen";
  public static final String M_BRUSH = "Brush";
  public static final String[] TOOLS_MENU = new String[]{M_PEN, M_BRUSH};

  private LinearLayout llView;

  private OnToolsFragmentInteractionListener mListener;

  public ToolsFragment() {
    // Required empty public constructor
  }

  public static ToolsFragment newInstance() {
    ToolsFragment fragment = new ToolsFragment();
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
    llView = (LinearLayout)inflater.inflate(R.layout.fragment_tools, container, false);

    ListView lvTools = (ListView)llView.findViewById(R.id.lvTools);

    lvTools.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, TOOLS_MENU));
    //TODO Set selected listener

    lvTools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO Check if already selected?  Can that happen?
        String s = adapterView.getItemAtPosition(i).toString();
        selectTool(s);
      }
    });

    return llView;
  }

  protected void selectTool(String tool) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          mListener.onSelectTool(tool);
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnToolsFragmentInteractionListener) {
      mListener = (OnToolsFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnToolsFragmentInteractionListener");
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
        Toast.makeText(ToolsFragment.this.getContext(), text, Toast.LENGTH_LONG).show();
      }
    });
  }
}
