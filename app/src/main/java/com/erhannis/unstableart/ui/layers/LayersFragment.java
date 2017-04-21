package com.erhannis.unstableart.ui.layers;

import android.content.Context;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.utils.Factory;
import com.erhannis.unstableart.FullscreenActivity;
import com.erhannis.unstableart.R;
import com.erhannis.unstableart.mechanics.context.GroupLayer;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.mechanics.context.StrokePL;
import com.terlici.dragndroplist.DragNDropCursorAdapter;
import com.terlici.dragndroplist.DragNDropCursorAdapter.RowType;
import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.IDd;
import com.terlici.dragndroplist.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
    test();
    updateView();
    return llView;
  }

  // This is annoying
  private static interface IDdTree extends Tree, IDd {
  }

  //TODO AAAARGH
  private Tree mTree = null;

  public <T extends Tree & IDd> void setTree(T tree) {
    mTree = (IDd & Tree)tree;
    updateView();
  }

  public void test() {
    if (llView != null) {
      String[] columns = new String[] { "_id", "type", "level", "text" };

      MatrixCursor matrixCursor = new MatrixCursor(columns);


      matrixCursor.addRow(new Object[] { 1, RowType.BEGIN, 0, "a" });
      matrixCursor.addRow(new Object[] { 5, RowType.NODE, 1, "aa" });
      matrixCursor.addRow(new Object[] { 6, RowType.BEGIN, 1, "ab" });
      matrixCursor.addRow(new Object[] { 9, RowType.NODE, 2, "aba" });
      matrixCursor.addRow(new Object[] { 10, RowType.NODE, 2, "abb" });
      matrixCursor.addRow(new Object[] { 8, RowType.END, 1, "/ab" });
      matrixCursor.addRow(new Object[] { 7, RowType.NODE, 1, "ac" });
      matrixCursor.addRow(new Object[] { 2, RowType.END, 0, "/a" });
      matrixCursor.addRow(new Object[] { 3, RowType.NODE, 0, "b" });
      matrixCursor.addRow(new Object[] { 4, RowType.NODE, 0, "c" });

      DragNDropListView list = (DragNDropListView)llView.findViewById(android.R.id.list);

      DragNDropCursorAdapter adapter = new DragNDropCursorAdapter(getContext(),
              R.layout.layers_row,
              matrixCursor,
              new String[]{"text"},
              new int[]{R.id.text},
              "type",
              "level",
              R.id.handler);

      list.setOnItemDragNDropListener(new DragNDropListView.OnItemDragNDropListener() {
        @Override
        public void onItemDrag(DragNDropListView parent, View view, int position, long id) {
          System.out.println("dragging item");
        }

        @Override
        public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
          showToast("dropped item " + startPosition + " -> " + endPosition);

          // Find parent
          //TODO Disallow put bag in bag
          ((MatrixCursor)list.getItemAtPosition(endPosition)).getString(3);
          //System.out.println();
        }
      });

      list.setDragNDropAdapter(adapter);
    }
  }

  //TODO Remove
  public static class TestIDd implements IDd<String> {
    private String id;

    public TestIDd(String id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return getId();
    }

    @Override
    public String getId() {
      return id;
    }
  }

  //TODO Remove
  public static class TestTree implements Tree, IDd<String> {
    private String id;
    private ArrayList<IDd> children = new ArrayList<IDd>();

    public TestTree(String id) {
      this.id = id;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public String toString() {
      return getId();
    }

    @Override
    public List getChildren() {
      return children;
    }

    public void addChild(IDd child) {
      children.add(child);
    }
  }

  public void updateView() {
    if (llView != null && mTree != null) {
      MatrixCursor matrixCursor = getCursorFromTree((Tree & IDd)mTree);
      // Still testing

      DragNDropListView list = (DragNDropListView)llView.findViewById(android.R.id.list);

      DragNDropCursorAdapter adapter = new DragNDropCursorAdapter(getContext(),
              R.layout.layers_row,
              matrixCursor,
              new String[]{"text"},
              new int[]{R.id.text},
              "type",
              "level",
              R.id.handler);

      list.setOnItemDragNDropListener(new DragNDropListView.OnItemDragNDropListener() {
        @Override
        public void onItemDrag(DragNDropListView parent, View view, int position, long id) {
          System.out.println("dragging item");
        }

        @Override
        public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
          // Note: occurs before item has moved.
          showToast("dropped item " + startPosition + " -> " + endPosition);

          FactoryHashMap<String, Integer> positions = new FactoryHashMap<String, Integer>(new Factory<Integer>() {
            @Override
            public Integer construct() {
              return 0;
            }
          });

          // Find parent
          //findParent();
          //TODO Disallow put bag in bag
          Stack<String> parentUuids = new Stack<>();
          int target = (startPosition < endPosition) ? endPosition + 1 : endPosition;
          String curParentUuid = null;
          for (int i = 0; i < target; i++) {
            //TODO Seems maybe kinda inefficient
            //"_id", "uuid", "type", "level", "text";
            MatrixCursor cursor = ((MatrixCursor)list.getItemAtPosition(i));
            String uuid = cursor.getString(1);
            RowType type = RowType.valueOf(cursor.getString(2));
            int level = cursor.getInt(3);
            switch (type) {
              case BEGIN:
                positions.put(curParentUuid, positions.get(curParentUuid) + 1);
                parentUuids.push(uuid);
                curParentUuid = uuid;
                break;
              case NODE:
                positions.put(curParentUuid, positions.get(curParentUuid) + 1);
                break;
              case END:
                parentUuids.pop();
                curParentUuid = parentUuids.peek();
                break;
              default:
                throw new IllegalArgumentException("Unhandled row type " + type);
            }
          }
          //TODO Maybe rely on end to block illegal?
          if (parentUuids.isEmpty()) {
            invalid();
          }
          String movingUuid = ((MatrixCursor)list.getItemAtPosition(startPosition)).getString(1);
          String newParentUuid = parentUuids.peek();
          while (!parentUuids.isEmpty()) {
            String uuid = parentUuids.pop();
            if (uuid.equals(movingUuid)) {
              invalid();
            }
          }
          moveLayer(movingUuid, newParentUuid, positions.get(newParentUuid));
        }
      });

      list.setDragNDropAdapter(adapter);
    }
  }

  //TODO Remove
  private void invalid() {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        setTree((Tree & IDd)mTree);
      }
    });
  }

  //TODO Remove
  private void moveLayer(String uuid, String parentUuid, int childPosition) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        IDd movingNode = null;
        Tree parentTree = null;

        Stack<Iterator> childStack = new Stack<>();

        //TODO Check no bag-in-bag

        childStack.push(mTree.getChildren().iterator());
        stackLoop: while (!childStack.isEmpty()) {
          Iterator children = childStack.pop();

          while (children.hasNext()) {
            //TODO Maybe catch possible exception?
            IDd child = (IDd) children.next();
            if (uuid.equals(child.getId())) {
              children.remove();
              movingNode = child;
              if (movingNode != null && parentTree != null) {
                break stackLoop;
              }
            }
            if (child instanceof Tree) {
              if (parentUuid.equals(child.getId())) {
                parentTree = (Tree)child;
                if (movingNode != null && parentTree != null) {
                  break stackLoop;
                }
              }

              childStack.push(children);
              childStack.push(((Tree)child).getChildren().iterator());
              continue stackLoop;
            }
          }
        }
        what if rearranged?
        parentTree.getChildren().add(childPosition, movingNode);
        setTree((Tree & IDd)mTree);
      }
    });
  }

  protected static <T extends Tree & IDd> MatrixCursor getCursorFromTree(T tree) {
    String[] columns = new String[]{"_id", "uuid", "type", "level", "text"};
    MatrixCursor matrixCursor = new MatrixCursor(columns);

    Stack<Iterator> childStack = new Stack<>();
    Stack<Object[]> endStack = new Stack<>();

    long rowId = 1L;

    matrixCursor.addRow(new Object[]{rowId++, tree.getId(), RowType.BEGIN, childStack.size(), tree.toString()});
    endStack.push(new Object[]{rowId++, tree.getId(), RowType.END, childStack.size(), tree.toString()});
    childStack.push(tree.getChildren().iterator());
    stackLoop: while (!childStack.isEmpty()) {
      Iterator children = childStack.pop();

      while (children.hasNext()) {
        //TODO Maybe catch possible exception?
        IDd child = (IDd) children.next();
        if (child instanceof Tree) {
          matrixCursor.addRow(new Object[]{rowId++, child.getId(), RowType.BEGIN, childStack.size() + 1, child.toString()});
          endStack.push(new Object[]{rowId++, child.getId(), RowType.END, childStack.size() + 1, child.toString()});
          childStack.push(children);
          childStack.push(((Tree)child).getChildren().iterator());
          continue stackLoop;
        } else {
          matrixCursor.addRow(new Object[]{rowId++, child.getId(), RowType.NODE, childStack.size() + 1, child.toString()});
        }
      }
      matrixCursor.addRow(endStack.pop());
    }

    return matrixCursor;
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
    public void onCreateLayer(String parentUuid, Layer child);
    public void onSelectLayer(String layerUuid);
    public void onMoveLayer(String layerUuid, String newParentUuid, int newPosition);
  }

  /*
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
        ArrayList<Layer> childLayers = ((GroupLayer)layer).iLayers;
        for (int i = childLayers.size() - 1; i >= 0; i--) {
          Layer childLayer = childLayers.get(i);
          TreeNode childNode = new TreeNode(new IconTreeItemHolder.IconTreeItem((childLayer instanceof GroupLayer) ? R.string.ic_folder : R.string.ic_drive_file, childLayer, childLayer.toString()));
          node.addChild(childNode);
          toProcess.offer(childNode);
        }
      }
    }

    return root;
  }
  */

  public void showToast(String text) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(LayersFragment.this.getContext(), text, Toast.LENGTH_LONG).show();
      }
    });
  }
}
