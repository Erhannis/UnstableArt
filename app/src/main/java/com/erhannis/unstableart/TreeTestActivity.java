package com.erhannis.unstableart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.erhannis.unstableart.mechanics.layers.Layer;
import com.erhannis.unstableart.ui.layers.LayersFragment;

public class TreeTestActivity extends AppCompatActivity implements LayersFragment.OnLayersFragmentInteractionListener<String> {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tree_test);

    LinearLayout llView = (LinearLayout)findViewById(R.id.activity_tree_test);

    LayersFragment myFragment = (LayersFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    /*
    myFragment.setTree(new LayersFragment.TestTree("a") {{
      addChild(new LayersFragment.TestTree("aa"){{
        addChild(new LayersFragment.TestIDd("aaa"));
        addChild(new LayersFragment.TestIDd("aab"));
      }});
      addChild(new LayersFragment.TestIDd("ab"));
      addChild(new LayersFragment.TestTree("ac"){{
        addChild(new LayersFragment.TestIDd("aca"));
        addChild(new LayersFragment.TestIDd("acb"));
        addChild(new LayersFragment.TestIDd("acc"));
      }});
    }});
    */
    /*
    FragmentManager fragMan = getSupportFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    LayersFragment layersFragment = new LayersFragment();
    fragTransaction.add(llView.getId(), layersFragment, "LayersFragment");
    fragTransaction.commit();

    layersFragment.setGroupLayer(new HistoryManager().rebuild());
    */
  }

  @Override
  public void onCreateLayer(String parentUuid, Layer child) {

  }

  @Override
  public void onSelectLayer(String layerUuid) {

  }

  @Override
  public void onMoveLayer(String layerUuid, String newParentUuid, int newPosition) {

  }

  @Override
  public void onShowHideLayer(String layerUuid, boolean visible) {

  }
}
