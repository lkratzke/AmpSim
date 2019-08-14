/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim.channel;

import com.geezertechnet.ampsim.supply.PowerSupply;

/**
 *
 * @author Loren Kratzke
 */
public class Transistor {
  
  // metadata describing the position in the circuit
  private final boolean positivePolarity;
  private final boolean outputTransistor;
  
  private final Transistor supplyCommutator;    // null if top rail
  private final PowerSupply collectorSupply; // The power supply that we draw current from
  private final PowerSupply emitterSupply;   // null if outputTransistor
  
  private final double leadVoltage;      // different for each strat
  
  private double collectorVoltage; // The greater of supplyCommutator.emitterVoltage or collectorSupply.voltage
  private double baseVoltage;      // Signal + leadVoltage
  private double emitterVoltage;   // Signal voltage if outputTransistor, otherwise the greater of baseVoltage or emitterSupply.voltage
  
  private boolean conducting;      // true if emitterVoltage greater than emitterSupply.voltage and signal is our polarity
  private double current;          // Equals load current if conducting or zero
  private boolean activeSupply;    // true if conducting and supplyCommutator is either null or not conducting
  private double totalJoulesDiss;  // Total joules dissapated by this device (divided by seconds equals average watts)
  
  private double joulesThisSecond;
  private int samplesPerSecond = 44100;
  private final int numSamples;
  
  private double wattsThisInstant;
  private final double[] wattsPerSecond;
  private int wattsPerSecondIndex;
  private int wattsPerSecondCount;
  
  
  public void processSignal(double signalVoltage, double signalCurrent) {
    baseVoltage = signalVoltage + leadVoltage;
    
    // Calculate emitter voltage
    if (outputTransistor) {
      emitterVoltage = signalVoltage;
    } else {
      if (positivePolarity) {
        emitterVoltage = Math.max(baseVoltage, emitterSupply.getVoltage());
      } else {
        emitterVoltage = Math.min(baseVoltage, emitterSupply.getVoltage());
      }
    }
    
    // Are we conducting?
    if (positivePolarity) {
      if (signalVoltage >= 0) {
        if (outputTransistor) {
          conducting = true;
        } else {
          conducting = (emitterVoltage > emitterSupply.getVoltage());
        }
      } else {
        conducting = false;
      }
    } else {
      if (signalVoltage <= 0) {
        if (outputTransistor) {
          conducting = true;
        } else {
          conducting = (emitterVoltage < emitterSupply.getVoltage());
        }
      } else {
        conducting = false;
      }
    }
    
    // Calculate collectorVoltage
    if (supplyCommutator == null) {
      collectorVoltage = collectorSupply.getVoltage();
    } else {
      // ****************
      // RECURSE UPWARD!!! 
      // The commutator above us needs to be able to tell us its emitterVoltage.
      // This will recurse up to the top rail and flow back down with emitterVoltage happiness for all along the way.
      // ****************
      supplyCommutator.processSignal(signalVoltage, signalCurrent);
      if (positivePolarity) {
        collectorVoltage = Math.max(supplyCommutator.getEmitterVoltage(), collectorSupply.getVoltage());
      } else {
        collectorVoltage = Math.min(supplyCommutator.getEmitterVoltage(), collectorSupply.getVoltage());
      }
    }
    
    // set activeSupply and current
    if (conducting) {
      activeSupply = supplyCommutator == null || !supplyCommutator.isConducting();
      current = signalCurrent;
    } else {
      activeSupply = false;
      current = 0;
    }
    
    // draw current (or not) from the collectorSupply
    if (activeSupply) {
      collectorSupply.drawCurrent(current);
    } else {
      // do this so it can still clock a sample and report stats over time
      collectorSupply.drawCurrent(0);
    }
    
    // Calculate power dissapation
    if (conducting) {
      wattsThisInstant = (collectorVoltage-emitterVoltage) * current;
    } else {
      wattsThisInstant = 0;
    }
    // is this ever needed?
    if (wattsThisInstant < 0) {
      wattsThisInstant = -wattsThisInstant;
    }
    double joulesThisInstant = wattsThisInstant/samplesPerSecond;
    totalJoulesDiss += joulesThisInstant;
    
    joulesThisSecond += joulesThisInstant;
    if ((++wattsPerSecondCount % samplesPerSecond) == 0) {
      wattsPerSecond[wattsPerSecondIndex++] = joulesThisSecond;
      joulesThisSecond = 0;
    }
  }
  
  public boolean isConducting() {
    return conducting;
  }
  
  public double getEmitterVoltage() {
    return emitterVoltage;
  }

  public double getCollectorVoltage() {
    return collectorVoltage;
  }

  public double getTotalJoulesDiss() {
    return totalJoulesDiss;
  }

  public double[] getWattsPerSecond() {
    return wattsPerSecond;
  }

  public double getWattsThisInstant() {
    return wattsThisInstant;
  }
  
  public Transistor(
          boolean positivePolarity, 
          boolean outputTransistor, 
          Transistor supplyCommutator, 
          PowerSupply collectorSupply, 
          PowerSupply emitterSupply, 
          double leadVoltage,
          int samplesPerSecond,
          int numSamples) {
    
    this.positivePolarity = positivePolarity;
    this.outputTransistor = outputTransistor;
    this.supplyCommutator = supplyCommutator;
    this.collectorSupply = collectorSupply;
    this.emitterSupply = emitterSupply;
    this.leadVoltage = leadVoltage;
    this.samplesPerSecond = samplesPerSecond;
    this.numSamples = numSamples;
    
    int numSeconds = numSamples/samplesPerSecond;
    if ((numSamples % samplesPerSecond) != 0) {
      numSeconds++;
    }
    wattsPerSecond = new double[numSeconds];
  }
}
