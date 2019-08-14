/*
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org>
 */
package com.geezertechnet.ampsim.wav;

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
