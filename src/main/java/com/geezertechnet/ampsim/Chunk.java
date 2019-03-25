/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
