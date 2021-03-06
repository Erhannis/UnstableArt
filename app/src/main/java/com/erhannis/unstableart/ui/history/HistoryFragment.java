package com.erhannis.unstableart.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.util.ObjectsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.erhannis.android.distributedui.DistributedUiActivity;
import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.android.orderednetworkview.Node;
import com.erhannis.android.orderednetworkview.OrderedNetworkView;
import com.erhannis.unstableart.R;
import com.erhannis.unstableart.history.HistoryNode;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.IntColor;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class HistoryFragment extends Fragment {
  private static final String TAG = "HistoryFragment";

  //TODO Seems like there's a bit of a trail of cached values....
  protected HistoryNode mRoot;
  protected LinkedHashMap<Marker, HistoryNode> mMarkerPositions;
  protected OrderedNetworkView<HistoryNode> onvHistory;

  private LinearLayout llView;

  private DistributedUiActivity mListener;

  public HistoryFragment() {
    // Required empty public constructor
  }

  public static HistoryFragment newInstance() {
    HistoryFragment fragment = new HistoryFragment();
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
    llView = (LinearLayout)inflater.inflate(R.layout.fragment_history, container, false);
    updateView();
    return llView;
  }

  /**
   * Reset history view to match the inputs.
   * Call this at least once before laying out the fragment.
   * Expects at least two markers, and the first is considered `view` and the second `edit`.
   * @param root
   * @param markerPositions
   */
  public void reset(HistoryNode root, LinkedHashMap<Marker, HistoryNode> markerPositions) {
    this.mRoot = root;
    this.mMarkerPositions = markerPositions;
    if (onvHistory != null) {
      onvHistory.reset(mRoot, mMarkerPositions);
    }
  }

  public OrderedNetworkView<HistoryNode> getOnvHistory() {
    //TODO Seems like I should either eschew this, or lean in fully
    return onvHistory;
  }

    public void updateView() {
    if (llView != null) {
      onvHistory = (OrderedNetworkView<HistoryNode>) llView.findViewById(R.id.onvHistory);
      onvHistory.reset(mRoot, mMarkerPositions);

      onvHistory.setOnDropMarkerListener(new OrderedNetworkView.OnDropMarkerListener<HistoryNode>() {
        @Override
        public void onDropMarker(Marker m, HistoryNode node) {
          //TODO This whole thing is sketchy and inconsistent with the distributed UI paradigm

          Iterator<Map.Entry<Marker, HistoryNode>> iter = mMarkerPositions.entrySet().iterator();
          //NOTE List markers
          Marker veMarker = iter.next().getKey();
          Marker viewMarker = iter.next().getKey();
          Marker editMarker = iter.next().getKey();
          Marker linkMarker = iter.next().getKey();
          Marker anchorMarker = iter.next().getKey();

          //TODO Document all this

          if (node == null && !ObjectsCompat.equals(m, anchorMarker)) {
            return; // We don't want any of the other markers to process a null node
          }

          if (ObjectsCompat.equals(m, linkMarker)) {
            // Link from edit node to target node
            HistoryNode fromNode = onvHistory.getMarkerPositions().get(editMarker);
            if (!Node.isAncestor(node, fromNode)) {
              //TODO The attempted path prioritization here can conflict slightly with the `preferred parent` thing.  Not sure which is correct, or how to resolve.
              fromNode.addChild(node); //TODO Is it legal to do this here, as opposed to only in the HistoryManager?
              //TODO In particular, consider that the change wouldn't be synced back across wireless

              onvHistory.doAddLink(fromNode, node);
            } else {
              //TODO Theoretically, it might be fine, as long as the *preferred path* didn't cycle
              Log.e(TAG, "Tried to link a cycle into history!");
            }
          } else if (ObjectsCompat.equals(m, veMarker)) {
            onvHistory.setMarkerPosition(viewMarker, node);
            onvHistory.setMarkerPosition(editMarker, node);
          } else {
            onvHistory.setMarkerPosition(m, node);
          }

          //TODO Also allow cut links?

          { // Ensure consistency of view/edit markers
            LinkedHashMap<Marker, HistoryNode> markerPositions = onvHistory.getMarkerPositions();
            HistoryNode editNode = markerPositions.get(editMarker);
            HistoryNode viewNode = markerPositions.get(viewMarker);
            if (!ObjectsCompat.equals(editNode, viewNode) && !Node.isAncestor(editNode, viewNode)) {
              if (ObjectsCompat.equals(m, viewMarker)) {
                onvHistory.setMarkerPosition(editMarker, viewNode);
              } else {
                onvHistory.setMarkerPosition(viewMarker, editNode);
              }
            }
          }
          { // Update path preference
            LinkedHashMap<Marker, HistoryNode> markerPositions = onvHistory.getMarkerPositions();
            HistoryNode editNode = markerPositions.get(editMarker);
            HistoryNode viewNode = markerPositions.get(viewMarker);
            //TODO This is the simplest reordering I have, but it may not be the most natural one.  Consider, after testing.
            boolean changed = false;
            changed |= Node.preferPath(mRoot, editNode);
            changed |= Node.preferPath(editNode, viewNode);
            if (changed) {
              System.err.println("ordering changed; refreshing view");
              onvHistory.refresh();
            }
            HistoryNode.rebuildPreferredParents(mRoot);
          }
          int priorityMarker;
          if (ObjectsCompat.equals(m, viewMarker)) {
            priorityMarker = 0;
          } else if (ObjectsCompat.equals(m, viewMarker)) {
            priorityMarker = 1;
          } else { // Default favors view
            // veMarker
            // linkMarker
            // anchorMarker
            priorityMarker = 0; //TODO Could potentially lead to problems
          }
          onSelectHistory(onvHistory.getMarkerPositions().get(viewMarker), onvHistory.getMarkerPositions().get(editMarker), onvHistory.getMarkerPositions().get(anchorMarker), priorityMarker);
        }
      });
    }
  }

  protected void onSelectHistory(HistoryNode viewNode, HistoryNode editNode, HistoryNode anchorNode, int priorityMarker) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          mListener.sendToHub("onSelectHistory", viewNode, editNode, anchorNode, priorityMarker);
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof DistributedUiActivity && ((DistributedUiActivity)context).implementsInterface(OnHistoryFragmentInteractionListener.class)) {
      mListener = (DistributedUiActivity) context;
    } else {
      throw new RuntimeException(context.toString() + " must report implementation OnHistoryFragmentInteractionListener");
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
  public interface OnHistoryFragmentInteractionListener {
    /**
     * History markers have been moved around.
     * @param viewNode
     * @param editNode
     * @param priority Which node was the primary target.  0 for view, 1 for edit.  Important for fixing invalid states without negating the user's action.
     */
    public void onSelectHistory(HistoryNode viewNode, HistoryNode editNode, HistoryNode anchorNode, int priority);
  }

  public void showToast(String text) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(HistoryFragment.this.getContext(), text, Toast.LENGTH_LONG).show();
      }
    });
  }
}
