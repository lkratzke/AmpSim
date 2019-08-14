/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim.supply;

/**
 *
 * @author Loren Kratzke
 */
public class IdealSupply implements PowerSupply {
  
  private final double restingVoltage;
  private final double[] wattsPerSecond;
  private double totalJoules;
  private final int samplesPerSecond;
  private final int numSamples;
  
  private final double sampleDuration;
  private double joulesThisSecond;
  private int jouleCount;
  private int wattsPerSecondCount;
  
  public IdealSupply(double restingVoltage, int samplesPerSecond, int numSamples) {
    this.restingVoltage = restingVoltage;
    this.samplesPerSecond = samplesPerSecond;
    this.sampleDuration = 1/samplesPerSecond;
    this.numSamples = numSamples;
    
    int numSeconds = numSamples/samplesPerSecond;
    if ((numSamples % samplesPerSecond) != 0) {
      numSeconds++;
    }
    wattsPerSecond = new double[numSeconds];
  }

  @Override
  public double getRestingVoltage() {
    return restingVoltage;
  }

  @Override
  public double getVoltage() {
    return restingVoltage;
  }

  @Override
  public double[] getWattsPerSecond() {
    return wattsPerSecond;
  }

  @Override
  public void drawCurrent(double loadCurrent) {
    double instantJoules = getVoltage() * loadCurrent * sampleDuration;
    totalJoules += instantJoules;
    joulesThisSecond += instantJoules;
    jouleCount++;
    
    if ((jouleCount % samplesPerSecond) == 0) {
      wattsPerSecond[wattsPerSecondCount++] = joulesThisSecond;
      jouleCount = 0;
      joulesThisSecond = 0;
    }
  }

  @Override
  public double getTotalJoules() {
    return totalJoules;
  }
  
}
