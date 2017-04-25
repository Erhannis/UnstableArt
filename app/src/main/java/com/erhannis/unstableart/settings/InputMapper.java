package com.erhannis.unstableart.settings;


import android.view.InputDevice;

import java.util.HashMap;

/**
 * Created by erhannis on 4/25/17.
 */

public class InputMapper {
  //TODO Initialization
  protected static final InputMapper mSingleton = new InputMapper();

  public static InputMapper getMapper() {
    return mSingleton;
  }

  protected final HashMap<String, Boolean> mDeviceDraws = new HashMap<>();
  protected final HashMap<String, Boolean> mDeviceMoves = new HashMap<>();

  public boolean deviceDraws(InputDevice device) {
    recordDevice(device);
    if (mDeviceDraws.containsKey(device.getDescriptor())) {
      return mDeviceDraws.get(device.getDescriptor());
    } else {
      // Default
      return device.getName().contains("Pen");
    }
  }

  public boolean deviceMoves(InputDevice device) {
    recordDevice(device);
    if (mDeviceMoves.containsKey(device.getDescriptor())) {
      return mDeviceMoves.get(device.getDescriptor());
    } else {
      // Default
      return device.getName().contains("Finger");
    }
  }

  protected void recordDevice(InputDevice device) {
    //TODO Do
  }
}
