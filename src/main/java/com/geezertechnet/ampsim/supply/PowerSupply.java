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
public interface PowerSupply {
  
  public double getRestingVoltage();
  
  public double getVoltage();
  
  public void drawCurrent(double amps);
  
  public double[] getWattsPerSecond();
  
  public double getTotalJoules();
  
}
