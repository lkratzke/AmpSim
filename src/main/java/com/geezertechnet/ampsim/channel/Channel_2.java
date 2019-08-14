/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim.channel;

import com.geezertechnet.ampsim.supply.IdealSupply;
import com.geezertechnet.ampsim.supply.PowerSupply;

/**
 *
 * @author Loren Kratzke
 */
public class Channel_2 {
  
  private final Transistor pComm;
  private final Transistor pOutput;
  private final Transistor nOutput;
  private final Transistor nComm;
  private final double load;
  
  public Channel_2(double v1, double v2, int samplesPerSecond, int numSamples, double load) {
    
    this.load = load;
    PowerSupply pCommSupply = new IdealSupply(v2, samplesPerSecond, numSamples);
    PowerSupply pOutputSupply = new IdealSupply(v1, samplesPerSecond, numSamples);
    PowerSupply nOutputSupply = new IdealSupply(-v1, samplesPerSecond, numSamples);
    PowerSupply nCommSupply = new IdealSupply(-v2, samplesPerSecond, numSamples);
    
    pComm = new Transistor(
            true, false, 
            null, 
            pCommSupply, 
            pOutputSupply, 
            1, samplesPerSecond, numSamples);
    
    pOutput = new Transistor(
            true, true, 
            pComm, 
            pOutputSupply, 
            null, 
            0, samplesPerSecond, numSamples);
    
    nComm = new Transistor(
            false, false, 
            null, 
            nCommSupply, 
            nOutputSupply, 
            -1, samplesPerSecond, numSamples);
    
    nOutput = new Transistor(
            false, true, 
            nComm, 
            nOutputSupply, 
            null, 
            0, samplesPerSecond, numSamples);
  }
  
  public void processSignal(double signalVoltage) {
    double signalCurrent = signalVoltage/load;
    pOutput.processSignal(signalVoltage, signalCurrent);
    nOutput.processSignal(signalVoltage, signalCurrent);
    
//    System.out.print(pComm.getCollectorVoltage());
//    System.out.print(",");
//    System.out.print(pOutput.getCollectorVoltage());
//    System.out.print(",");
//    System.out.print(signalVoltage);
//    System.out.print(",");
//    System.out.print(nOutput.getCollectorVoltage());
//    System.out.print(",");
//    System.out.print(nComm.getCollectorVoltage());
//    System.out.print(",");
//    System.out.print(pOutput.getWattsThisInstant());
//    System.out.print(",");
//    System.out.print(nOutput.getWattsThisInstant());
//    System.out.print(",");
//    System.out.print(pComm.getWattsThisInstant());
//    System.out.print(",");
//    System.out.println(nComm.getWattsThisInstant());
  }

  public Transistor getpComm() {
    return pComm;
  }

  public Transistor getpOutput() {
    return pOutput;
  }

  public Transistor getnOutput() {
    return nOutput;
  }

  public Transistor getnComm() {
    return nComm;
  }
  
}
