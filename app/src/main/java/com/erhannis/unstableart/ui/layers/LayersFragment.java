package com.erhannis.unstableart.ui.layers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erhannis.unstableart.R;
import com.erhannis.unstableart.mechanics.context.GroupLayer;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.StrokePL;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LayersFragment.OnLayersFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LayersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LayersFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private LinearLayout llView;

  private OnLayersFragmentInteractionListener mListener;

  public LayersFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment LayersFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static LayersFragment newInstance(String param1, String param2) {
    LayersFragment fragment = new LayersFragment();
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
    llView = (LinearLayout)inflater.inflate(R.layout.fragment_layers, container, false);
    updateView();
    return llView;
  }

  private GroupLayer mGroupLayer = null;

  public void setGroupLayer(GroupLayer layer) {
    mGroupLayer = layer;
    updateView();
  }

  public void updateView() {
    if (llView != null && mGroupLayer != null) {
      llView.removeAllViews();
      TreeNode root = constructTree(mGroupLayer);

      /*
      root = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, null, "node 0"));
      root.setSelectable(false);
      root.addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, null, "node 0.0")){{
        addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, null, "node 0.0.0")));
        addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, null, "node 0.0.1")));
        addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, null, "node 0.0.2")));
      }});
      root.addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, null, "node 0.1")){{
        addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, null, "node 0.1.0")));
        addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, null, "node 0.1.1")));
        addChild(new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, null, "node 0.1.2")));
      }});
      */

      AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
      tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
      tView.setDefaultViewHolder(IconTreeItemHolder.class);
      /*
      tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
          selectLayer(((IconTreeItemHolder.IconTreeItem)value).layer);
        }
      });
      */
      tView.setDefaultNodeLongClickListener(new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
          Layer parent = ((IconTreeItemHolder.IconTreeItem)value).layer;
          if (parent instanceof GroupLayer) {
            Layer child = new StrokePL();
            createLayer((GroupLayer)parent, child);
          } else {
            selectLayer(parent);
          }
          return true;
        }
      });
      llView.addView(textView(getActivity(), "before"));
      llView.addView(tView.getView());
      llView.addView(textView(getActivity(), "after"));
    }
  }

  public static TextView textView(Context context, String text) {
    TextView textView = new TextView(context);
    textView.setText(text);
    return textView;
  }

  private void createLayer(GroupLayer parent, Layer child) {
    if (mListener != null) {
      mListener.onCreateLayer(parent.uuid, child);
    }
  }

  private void selectLayer(Layer layer) {
    if (mListener != null) {
      mListener.onSelectLayer(layer.uuid);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnLayersFragmentInteractionListener) {
      mListener = (OnLayersFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
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
  public interface OnLayersFragmentInteractionListener {
    // TODO: Update argument type and name
    void onCreateLayer(String parentUuid, Layer child);
    void onSelectLayer(String layerUuid);
  }

  protected TreeNode constructTree(GroupLayer rootLayer) {
    // I've started to become wary of recursive functions
    LinkedList<TreeNode> toProcess = new LinkedList<>();


    TreeNode root = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, null, "root"));
    root.setSelectable(false);
    TreeNode newRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, rootLayer, rootLayer.toString()));
    root.addChild(newRoot);
    toProcess.offer(newRoot);

    while (!toProcess.isEmpty()) {
      TreeNode node = toProcess.poll();
      Layer layer = ((IconTreeItemHolder.IconTreeItem)node.getValue()).layer;
      //TODO Set text, image, etc.?
      if (layer instanceof GroupLayer) {
        for (Layer childLayer : ((GroupLayer)layer).iLayers) {
          TreeNode childNode = new TreeNode(new IconTreeItemHolder.IconTreeItem((childLayer instanceof GroupLayer) ? R.string.ic_folder : R.string.ic_drive_file, childLayer, childLayer.toString()));
          node.addChild(childNode);
          toProcess.offer(childNode);
        }
      }
    }

    return root;
  }
}
