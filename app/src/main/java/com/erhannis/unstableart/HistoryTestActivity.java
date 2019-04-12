package com.erhannis.unstableart;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.android.orderednetworkview.OrderedNetworkView;
import com.erhannis.unstableart.history.AddStrokePHN;
import com.erhannis.unstableart.history.HistoryNode;
import com.erhannis.unstableart.history.RootHN;
import com.erhannis.unstableart.history.SetColorSMHN;
import com.erhannis.unstableart.mechanics.color.Color;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.stroke.Stroke;

public class HistoryTestActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history_test);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    OrderedNetworkView<HistoryNode> onvHistory = (OrderedNetworkView<HistoryNode>) findViewById(R.id.onvHistory);
    setSupportActionBar(toolbar);

    RootHN rootHN = new RootHN();
    SetColorSMHN colorSMHN1 = new SetColorSMHN(new DoublesColor(1, 0, 1, 0));
    SetColorSMHN colorSMHN2 = new SetColorSMHN(new DoublesColor(1, 0, 1, 1));
    AddStrokePHN s1 = new AddStrokePHN(new Stroke());
    AddStrokePHN s2 = new AddStrokePHN(new Stroke());
    AddStrokePHN s3 = new AddStrokePHN(new Stroke());
    AddStrokePHN s4 = new AddStrokePHN(new Stroke());
    AddStrokePHN s2_1 = new AddStrokePHN(new Stroke());
    AddStrokePHN s2_2 = new AddStrokePHN(new Stroke());
    SetColorSMHN colorSMHN3 = new SetColorSMHN(new DoublesColor(1, 0, 0.5, 0.5));
    rootHN.addChild(colorSMHN1);
    colorSMHN1.addChild(colorSMHN2);
    colorSMHN2.addChild(s1);
    s1.addChild(s2);
    s2.addChild(s3);
    s3.addChild(s4);
    s2.addChild(s2_1);
    s2_1.addChild(s2_2);
    s2_2.addChild(colorSMHN3);
    onvHistory.reset(rootHN, new Marker[]{});

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });
  }

}
