/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Loren Kratzke
 */
public class Util {
  
  // Handle the case where we are passed only 2 bytes but want an int.
  public static int getLittleEndianInt(byte[] b) {
    byte[] padded;
    if (b.length == 2) {
      padded = new byte[4];
      padded[0] = b[0];
      padded[1] = b[1];
    } else {
      padded = b;
    }
    ByteBuffer bb = ByteBuffer.wrap(padded);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    return bb.getInt();
  }
  
  public static short getLittleEndianShort(byte[] b) {
    ByteBuffer bb = ByteBuffer.wrap(b);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    return bb.getShort();
  }
  
}
