package com.erhannis.unstableart;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.erhannis.unstableart.history.HistoryManager;
import com.erhannis.unstableart.mechanics.context.Layer;
import com.erhannis.unstableart.ui.layers.LayersFragment;

public class TreeTestActivity extends AppCompatActivity implements LayersFragment.OnLayersFragmentInteractionListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tree_test);

    LinearLayout llView = (LinearLayout)findViewById(R.id.activity_tree_test);

    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    LayersFragment layersFragment = new LayersFragment();
    fragTransaction.add(llView.getId(), layersFragment, "LayersFragment");
    fragTransaction.commit();

    layersFragment.setGroupLayer(new HistoryManager().rebuild());
  }

  @Override
  public void onCreateLayer(String parentUuid, Layer child) {

  }

  @Override
  public void onSelectLayer(String layerUuid) {

  }
}
