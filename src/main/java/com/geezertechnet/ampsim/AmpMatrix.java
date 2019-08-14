/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim;

import com.geezertechnet.ampsim.channel.Channel_2;
import com.geezertechnet.ampsim.wav.WaveData;
import java.io.IOException;

/**
 *
 * @author Loren Kratzke
 */
public class AmpMatrix {
  
  public static void main(String[] args) {
    AmpMatrix amp = new AmpMatrix();
    String path = "C:\\vlad\\blue_missions\\M1_UP\\7_M1\\Music\\The Dark Side Of The Moon wav\\Track 5.wav";
//    String path = "C:\\Users\\loren\\Music\\Unknown artist\\Unknown album (7-28-2019 3-37-27 PM)\\01 Track 1.wav";
    amp.run(path);
  }
  
  public void run(String path) {
    try {
      WaveData waveData = new WaveData(path);
      System.out.println(waveData.getReport());
      short[][] samples = waveData.getDataSamples();
      System.out.println(path);

//      short[][] samples = createShortSine(1000);
      
      
      for (int v=5; v<31; v++) {
        double efficiency = analyze(samples, v, 40);
//        System.out.println("v1: " + v + ", efficiency: " + efficiency*100 + "%");
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }
  
  private double analyze(short[][] samples, double v1, double v2) {
    final int numSamples = samples[0].length;
    final int samplesPerSecond = 44100;
    final double loadOhms = 8;
    final double normal = 32768;
    final double numSeconds = (double)numSamples/(double)samplesPerSecond;
    final double amplitude = 40;
    
    double leftLoadJoules = 0;
    double rightLoadJoules = 0;
    
    Channel_2 left = new Channel_2(v1, 41, samplesPerSecond, numSamples, loadOhms);
    Channel_2 right = new Channel_2(40, 41, samplesPerSecond, numSamples, loadOhms);
    
    for (int n=0; n<numSamples; n++) {
      double leftSignal = (double)samples[0][n] * amplitude / normal;
      double rightSignal = (double)samples[1][n] * amplitude / normal;
      left.processSignal(leftSignal);
      right.processSignal(rightSignal);
      leftLoadJoules += (leftSignal * leftSignal / loadOhms / samplesPerSecond);
      rightLoadJoules += (rightSignal * rightSignal / loadOhms / samplesPerSecond);
    }
    
    double leftPComWatts = left.getpComm().getTotalJoulesDiss()/numSeconds;
    double leftPOutputWatts = (left.getpOutput().getTotalJoulesDiss()/numSeconds);
    double leftNOutputWatts = (left.getnOutput().getTotalJoulesDiss()/numSeconds);
    double leftNComWatts = (left.getnComm().getTotalJoulesDiss()/numSeconds);
    double totalLeftDiss = leftPComWatts+leftPOutputWatts+leftNOutputWatts+leftNComWatts;
    double leftOutputWatts = leftLoadJoules/numSeconds;
    double leftConsumedWatts = totalLeftDiss+leftOutputWatts;
    double leftEfficiency = leftOutputWatts/leftConsumedWatts;

    double rightPComWatts = right.getpComm().getTotalJoulesDiss()/numSeconds;
    double rightPOutputWatts = right.getpOutput().getTotalJoulesDiss()/numSeconds;
    double rightNOutputWatts = right.getnOutput().getTotalJoulesDiss()/numSeconds;
    double rightNComWatts = right.getnComm().getTotalJoulesDiss()/numSeconds;
    double totalRightDiss = rightPComWatts+rightPOutputWatts+rightNOutputWatts+rightNComWatts;
    double rightOutputWatts = rightLoadJoules/numSeconds;
    double rightConsumedWatts = totalRightDiss+rightOutputWatts;
    double rightEfficiency = rightOutputWatts/rightConsumedWatts;
    
    System.out.println();
    System.out.println("  leftOutputWatts: " + leftOutputWatts);
    System.out.println("  leftConsumedWatts: " + leftConsumedWatts);
    System.out.println("  pCom watts: " + leftPComWatts);
    System.out.println("  pOut watts: " + leftPOutputWatts);
    System.out.println("  nOut watts: " + leftNOutputWatts);
    System.out.println("  nCom watts: " + leftNComWatts);
    System.out.println("v1: " + v1 + ", efficiency: " + leftEfficiency*100 + "%");
    
    System.out.println();
    System.out.println("  rightOutputWatts: " + rightOutputWatts);
    System.out.println("  rightConsumedWatts: " + rightConsumedWatts);
    System.out.println("  pCom watts: " + rightPComWatts);
    System.out.println("  pOut watts: " + rightPOutputWatts);
    System.out.println("  nOut watts: " + rightNOutputWatts);
    System.out.println("  nCom watts: " + rightNComWatts);
    System.out.println("v1: " + 40 + ", efficiency: " + rightEfficiency*100 + "%");
    
    return (leftEfficiency)/2;
  }
  
  private double[] createSine(int numSamples) {
    double[] samples = new double[numSamples+1];
    double step = 3.14159 * 2 / numSamples;
    for (int n=0; n<(numSamples+1); n++) {
      samples[n] = Math.sin(step * n);
//      System.out.println(samples[n]);
    }
    return samples;
  }
  
  private short[][] createShortSine(int numSamples) {
    double[] dsamps = createSine(numSamples);
    short[][] ssamps = new short[2][numSamples];
    for (int n=0; n<numSamples; n++) {
      ssamps[0][n] = (short)(dsamps[n]*32768);
      ssamps[1][n] = ssamps[0][n];
    }
    return ssamps;
  }
  
}
