package com.erhannis.unstableart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.erhannis.android.orderednetworkview.Marker;
import com.erhannis.android.orderednetworkview.OrderedNetworkView;
import com.erhannis.unstableart.history.AddStrokePHN;
import com.erhannis.unstableart.history.HistoryNode;
import com.erhannis.unstableart.history.RootHN;
import com.erhannis.unstableart.history.SetColorSMHN;
import com.erhannis.unstableart.mechanics.color.DoublesColor;
import com.erhannis.unstableart.mechanics.stroke.Stroke;
import com.erhannis.unstableart.ui.history.EditMarker;
import com.erhannis.unstableart.ui.history.ViewMarker;

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
    AddStrokePHN s2_3 = new AddStrokePHN(new Stroke());
    SetColorSMHN colorSMHN3 = new SetColorSMHN(new DoublesColor(1, 0, 0.5, 0.5));
    AddStrokePHN s2_4 = new AddStrokePHN(new Stroke());
    SetColorSMHN colorSMHN4 = new SetColorSMHN(new DoublesColor(1, 0, 0, 0));
    SetColorSMHN colorSMHN5 = new SetColorSMHN(new DoublesColor(1, 1, 0, 0));
    rootHN.addChild(colorSMHN1);
    colorSMHN1.addChild(colorSMHN2);
    colorSMHN2.addChild(s1);
    s1.addChild(s2);
    s2.addChild(s3);
    s3.addChild(s4);
    s2.addChild(s2_1);
    s2_1.addChild(s2_2);
    s2_2.addChild(s2_3);
    s2_3.addChild(colorSMHN3);
    colorSMHN3.addChild(s2_4);
    s2_1.addChild(colorSMHN4);
    colorSMHN4.addChild(colorSMHN5);
    colorSMHN5.addChild(s2_2);
    ViewMarker viewMarker = new ViewMarker();
    EditMarker editMarker = new EditMarker();

    onvHistory.reset(rootHN, new Marker[]{viewMarker, editMarker});
    onvHistory.setMarkerPosition(viewMarker, s2_4);
    onvHistory.setMarkerPosition(editMarker, colorSMHN5);

    onvHistory.setOnDropMarkerListener(new OrderedNetworkView.OnDropMarkerListener<HistoryNode>() {
      @Override
      public void onDropMarker(Marker m, HistoryNode node) {
        onvHistory.setMarkerPosition(m, node);
      }
    });

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onvHistory.requestLayout();
      }
    });
  }
}
