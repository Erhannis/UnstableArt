package com.erhannis.unstableart.ui.layers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.erhannis.unstableart.R;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        //TODO Do?
        /*
        view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add layer?
                TreeNode newFolder = new TreeNode(new IconTreeItem(R.string.ic_folder, null, "New Folder"));
                getTreeView().addNode(node, newFolder);
            }
        });
        */

        //TODO Allow?
        /*
        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeView().removeNode(node);
            }
        });
        */

        //if My computer
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        //TODO Do?
        /*
        public static interface IconTreeItemInterface {
            public void onClickAdd(IconTreeItem item);
            public void onClickDelete(IconTreeItem item);
        }
        */

        public int icon;
        public Layer layer;
        public String text;


        public IconTreeItem(int icon, Layer layer, String text) {
            this.icon = icon;
            this.layer = layer;
            this.text = text;
        }
    }
}
