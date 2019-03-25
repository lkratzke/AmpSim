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
package com.geezertechnet.ampsim;

/**
 * Generic bean to encapsulate chunks that are not of type fmt or data.
 * @author Loren Kratzke
 */
public class Chunk {
  
  private byte[] id = new byte[4];
  private byte[] size = new byte[4];
  private byte[] data;
  
  // Getters that return converted values (Strings and ints instead of byte[])
  
  public String getIdString() {
    return new String(id);
  }
  
  public int getSizeInt() {
    return Util.getLittleEndianInt(size);
  }
  
  // This one could produce garbage Strings with unprintable characters
  public String getDataString() {
    return new String(data);
  }
  
  // Getters for raw byte[] values

  public byte[] getId() {
    return id;
  }

  public void setId(byte[] id) {
    this.id = id;
  }

  public byte[] getSize() {
    return size;
  }

  public void setSize(byte[] size) {
    this.size = size;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
}
