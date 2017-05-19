package com.erhannis.unstableart.ui.layers;

import android.content.Context;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhannis.android.ekandroid.ClassScanner;
import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.utils.Factory;
import com.erhannis.unstableart.R;
import com.erhannis.unstableart.mechanics.layers.GroupLayer;
import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.mechanics.layers.StrokePL;
import com.erhannis.unstableart.mechanics.layers.UACanvas;
import com.terlici.dragndroplist.DragNDropCursorAdapter;
import com.terlici.dragndroplist.DragNDropCursorAdapter.RowType;
import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.IDd;
import com.terlici.dragndroplist.Tree;
import com.terlici.dragndroplist.Visible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java8.util.function.BiConsumer;

/**
 * //TODO This is becoming horrifying.  Fix it.
 */
public class LayersFragment<ID> extends Fragment {
  private static final String[] COLUMNS = new String[]{"_id", "uuid", "type", "level", "text", "selected", "visible"};
  private static final int COL_UUID = 1;
  private static final int COL_TYPE = 2;
  private static final int COL_LEVEL = 3;
  private static final int COL_TEXT = 4;
  private static final int COL_SELECTED = 5;
  private static final int COL_VISIBLE = 6;

  private LinearLayout llView;

  private OnLayersFragmentInteractionListener<ID> mListener;

  public LayersFragment() {
    // Required empty public constructor
  }

  public static LayersFragment newInstance() {
    LayersFragment fragment = new LayersFragment();
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
    llView = (LinearLayout)inflater.inflate(R.layout.fragment_layers, container, false);


    llView.findViewById(R.id.btnAddStrokeLayer).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Layer newLayer = new StrokePL();
        createLayer(((IDd<ID>)mTree).getId(), newLayer);
      }
    });

    llView.findViewById(R.id.btnAddGroupLayer).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Layer newLayer = new GroupLayer();
        createLayer(((IDd<ID>)mTree).getId(), newLayer);
      }
    });

    llView.findViewById(R.id.btnAddOtherLayer).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO This will need to change, for settings, etc.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        List<Class> classes = new ArrayList<Class>();
        try {
          classes = ClassScanner.getConcreteDescendants(getContext(), Layer.class, null);
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }

        classes.remove(UACanvas.class);

        ArrayList<String> classNames = new ArrayList<String>();
        for (Class<?> clazz : classes) {
          classNames.add(clazz.getName());
        }
        String[] layerTypes = new String[]{};
        layerTypes = classNames.toArray(layerTypes);

        final List<Class> fClasses = classes;

        builder.setTitle("Pick a layer type")
                .setItems(layerTypes, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    try {
                      Layer newLayer = (Layer)fClasses.get(which).newInstance();
                      createLayer(((IDd<ID>)mTree).getId(), newLayer);
                    } catch (java.lang.InstantiationException e) {
                      e.printStackTrace();
                      showToast("Error creating layer:\n" + e.getMessage());
                    } catch (IllegalAccessException e) {
                      e.printStackTrace();
                      showToast("Error creating layer:\n" + e.getMessage());
                    }
                  }
                });
        builder.show();
      }
    });

    updateView();
    return llView;
  }

  //TODO AAAARGH
  private Tree mTree = null;
  private ID mSelectedId = null;

  public <T extends Tree & IDd<ID> & Visible> void setTree(T tree, ID selectedId) {
    mTree = (IDd<ID> & Tree & Visible)tree;
    mSelectedId = selectedId;
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        updateView();
      }
    });
  }

  public void updateView() {
    if (llView != null && mTree != null) {
      MatrixCursor matrixCursor = getCursorFromTree((Tree & IDd<ID> & Visible)mTree, mSelectedId);
      // Still testing

      DragNDropListView list = (DragNDropListView)llView.findViewById(android.R.id.list);

      DragNDropCursorAdapter adapter = new DragNDropCursorAdapter(getContext(),
              R.layout.layers_row,
              matrixCursor,
              new String[]{"text"},
              new int[]{R.id.text},
              "type",
              "level",
              R.id.handler,
              "selected",
              R.id.selected,
              "visible",
              R.id.cbCheckBox,
              new BiConsumer<Integer, Boolean>() {
                @Override
                public void accept(Integer i, Boolean visible) {
                  MatrixCursor cursor = ((MatrixCursor)list.getItemAtPosition(i));
                  String uuid = cursor.getString(COL_UUID);
                  showHideLayer(uuid, visible);
                }
              }
      );

      list.setOnItemDragNDropListener(new DragNDropListView.OnItemDragNDropListener() {
        @Override
        public void onItemDrag(DragNDropListView parent, View view, int position, long id) {
          System.out.println("dragging item");
        }

        @Override
        public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
          // Note: occurs before item has moved.
          //showToast("dropped item " + startPosition + " -> " + endPosition);

          String oldParentUuid = getParentUuid(list, null, startPosition);
          if (oldParentUuid == null) {
            invalid();
            return;
          }

          FactoryHashMap<String, Integer> positions = new FactoryHashMap<String, Integer>(new Factory<Integer>() {
            @Override
            public Integer construct() {
              return 0;
            }
          });
          int target = (startPosition < endPosition) ? endPosition + 1 : endPosition;
          String newParentUuid = getParentUuid(list, positions, target);
          if (newParentUuid == null) {
            invalid();
            return;
          }
          String movingUuid = ((MatrixCursor)list.getItemAtPosition(startPosition)).getString(COL_UUID);
          /* // Uncomment to check bag-in-bag
          while (!parentUuids.isEmpty()) {
            String uuid = parentUuids.pop();
            if (uuid.equals(movingUuid)) {
              invalid();
              return;
            }
          }
          */
          int newPosition = positions.get(newParentUuid);
          if (oldParentUuid.equals(newParentUuid) && startPosition < endPosition) {
            newPosition--;
          }
          moveLayer(movingUuid, newParentUuid, newPosition);
          return;
        }
      });

      list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          //TODO Show which one selected
          String uuid = ((MatrixCursor)list.getItemAtPosition(position)).getString(COL_UUID);
          if (uuid != null) {
            selectLayer(uuid);
          }
        }
      });

      list.setDragNDropAdapter(adapter);
    }
  }

  private static String getParentUuid(DragNDropListView list, FactoryHashMap<String, Integer> positions, int target) {
    if (positions == null) {
      positions = new FactoryHashMap<String, Integer>(new Factory<Integer>() {
        @Override
        public Integer construct() {
          return 0;
        }
      });
    }
    Stack<String> parentUuids = new Stack<>();
    String curParentUuid = null;
    for (int i = 0; i < target; i++) {
      //TODO Seems maybe kinda inefficient
      MatrixCursor cursor = ((MatrixCursor)list.getItemAtPosition(i));
      String uuid = cursor.getString(COL_UUID);
      RowType type = RowType.valueOf(cursor.getString(COL_TYPE));
      int level = cursor.getInt(COL_LEVEL);
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
          if (!parentUuids.isEmpty()) {
            curParentUuid = parentUuids.peek();
          } else {
            curParentUuid = null;
          }
          break;
        default:
          throw new IllegalArgumentException("Unhandled row type " + type);
      }
    }
    //TODO Maybe rely on end to block illegal?
    if (parentUuids.isEmpty()) {
      return null;
    }
    String newParentUuid = parentUuids.peek();
    return newParentUuid;
  }

  private void invalid() {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        setTree((Tree & IDd<ID> & Visible)mTree, mSelectedId);
      }
    });
  }

  private void moveLayer(String uuid, String parentUuid, int childPosition) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          //TODO Fix if we change ID to not String
          mListener.onMoveLayer((ID)uuid, (ID)parentUuid, childPosition);
        }
      }
    });
  }

  private void createLayer(ID parentId, Layer child) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          mListener.onCreateLayer(parentId, child);
        }
      }
    });
  }

  private void selectLayer(String layerUuid) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          //TODO Fix if we change ID to not String
          mListener.onSelectLayer((ID)layerUuid);
        }
      }
    });
  }

  private void showHideLayer(String uuid, boolean visible) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (mListener != null) {
          //TODO Fix if we change ID to not String
          mListener.onShowHideLayer((ID)uuid, visible);
        }
      }
    });
  }

  protected static <ID, T extends Tree & IDd<ID> & Visible> MatrixCursor getCursorFromTree(T tree, ID selectedId) {
    String[] columns = COLUMNS;
    MatrixCursor matrixCursor = new MatrixCursor(columns);

    Stack<Iterator> childStack = new Stack<>();
    Stack<Object[]> endStack = new Stack<>();

    long rowId = 1L;

    matrixCursor.addRow(new Object[]{rowId++, tree.getId(), RowType.BEGIN, childStack.size(), tree.toString(), tree.getId().equals(selectedId), tree.isVisible()});
    endStack.push(new Object[]{rowId++, tree.getId(), RowType.END, childStack.size(), tree.toString(), false, tree.isVisible()});
    childStack.push(tree.getChildren().iterator());
    stackLoop: while (!childStack.isEmpty()) {
      Iterator children = childStack.pop();

      while (children.hasNext()) {
        //TODO Maybe catch possible exception?
        IDd<ID> child = (IDd<ID> & Visible) children.next();
        if (child instanceof Tree) {
          matrixCursor.addRow(new Object[]{rowId++, child.getId(), RowType.BEGIN, childStack.size() + 1, child.toString(), child.getId().equals(selectedId), ((Visible)child).isVisible()});
          endStack.push(new Object[]{rowId++, child.getId(), RowType.END, childStack.size() + 1, child.toString(), false, ((Visible)child).isVisible()});
          childStack.push(children);
          childStack.push(((Tree)child).getChildren().iterator());
          continue stackLoop;
        } else {
          matrixCursor.addRow(new Object[]{rowId++, child.getId(), RowType.NODE, childStack.size() + 1, child.toString(), child.getId().equals(selectedId), ((Visible)child).isVisible()});
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

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnLayersFragmentInteractionListener) {
      mListener = (OnLayersFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnLayersFragmentInteractionListener");
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
  public interface OnLayersFragmentInteractionListener<ID> {
    // TODO: Update argument type and name
    public void onCreateLayer(ID parentUuid, Layer child);
    public void onSelectLayer(ID layerUuid);
    public void onMoveLayer(ID layerUuid, ID newParentUuid, int newPosition);
    public void onShowHideLayer(ID layerUuid, boolean visible);
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
