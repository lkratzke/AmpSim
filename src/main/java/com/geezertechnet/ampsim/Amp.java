/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim;

import java.io.IOException;

/**
 *
 * @author Loren Kratzke
 */
public class Amp {
  
  public static void main(String[] args) {
    Amp amp = new Amp();
    amp.processWav(args[0]);
  }
  
  public void processWav(String path) {
    try {
      WaveData waveData = new WaveData(path);
      System.out.println(waveData.getReport());
      System.out.println();
      
      for (int n=0; n<44; n++) {
        System.out.println(waveData.getDataSamples()[0][n]);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  
}
