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

import com.geezertechnet.ampsim.channel.Channel_2;
import com.geezertechnet.ampsim.wav.WaveData;
import java.io.IOException;

/**
 *
 * @author Loren Kratzke
 */
public class Amp {
  
  public static void main(String[] args) {
    Amp amp = new Amp();
//    amp.processWav(args[0]);
//    double[] samples = amp.createSine(80);
//    amp.processSine(samples);
    
//    String path = "C:\\vlad\\blue_missions\\M1_UP\\7_M1\\Music\\The Dark Side Of The Moon wav\\Track 5.wav";
    String path = "C:\\Users\\loren\\Music\\Unknown artist\\Unknown album (7-28-2019 3-37-27 PM)\\01 Track 1.wav";
    amp.analyzeSong(path);
  }
  
  public void analyzeSong(String path) {
    try {
      WaveData waveData = new WaveData(path);
      System.out.println(waveData.getReport());
      short[][] samples = waveData.getDataSamples();
      
//      short[][] samples = createShortSine(79); // gives 80 samples
      
      final int numSamples = samples[0].length;
      final int samplesPerSecond = 44100;
      final double loadOhms = 8;
      final double normal = 32768;
      final double numSeconds = (double)numSamples/(double)samplesPerSecond;
      final double amplitude = 40;
      
      Channel_2 left = new Channel_2(40, 40, samplesPerSecond, numSamples, loadOhms);
      Channel_2 right = new Channel_2(28, 40, samplesPerSecond, numSamples, loadOhms);
      double leftLoadJoules = 0;
      double rightLoadJoules = 0;
      double maxSignal = 0;
      double avSignal = 0;
      
      for (int n=0; n<numSamples; n++) {
        double leftSignal = (double)samples[0][n] * amplitude / normal;
        double rightSignal = (double)samples[1][n] * amplitude / normal;
        maxSignal = Math.max(maxSignal, leftSignal);
        avSignal += (Math.abs(leftSignal)/numSamples);
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
      double rightNCommWatts = right.getnComm().getTotalJoulesDiss()/numSeconds;
      double totalRightDiss = rightPComWatts+rightPOutputWatts+rightNOutputWatts+rightNCommWatts;
      double rightOutputWatts = rightLoadJoules/numSeconds;
      double rightConsumedWatts = totalRightDiss+rightOutputWatts;
      double rightEfficiency = rightOutputWatts/rightConsumedWatts;
      
      System.out.println("Max Signal = " + maxSignal);
      System.out.println("Av Signal = " + avSignal);
      System.out.println();
      
      System.out.println("Left channel output watts: " + leftOutputWatts);
      System.out.println("Left channel diss watts: " + totalLeftDiss);
      System.out.println("Left channel consumed watts: " + leftConsumedWatts);
      System.out.println("Left channel efficiency: " + (leftEfficiency*100) + "%");
      System.out.println();
      
      System.out.println("Right channel output watts: " + rightOutputWatts);
      System.out.println("Right channel diss watts: " + totalRightDiss);
      System.out.println("Right channel consumed watts: " + rightConsumedWatts);
      System.out.println("Right channel efficiency: " + (rightEfficiency*100) + "%");
      System.out.println();
      
      System.out.println("LEFT");
      System.out.println("Pos Comm watts diss: " + leftPComWatts);
      System.out.println("Pos Output watts diss: " + leftPOutputWatts);
      System.out.println("Neg Output watts diss: " + leftNOutputWatts);
      System.out.println("Neg Comm watts diss: " + leftNComWatts);
      System.out.println();
      
      System.out.println("RIGHT");
      System.out.println("Pos Comm watts diss: " + rightPComWatts);
      System.out.println("Pos Output watts diss: " + rightPOutputWatts);
      System.out.println("Neg Output watts diss: " + rightNOutputWatts);
      System.out.println("Neg Comm watts diss: " + rightNCommWatts);
      
      
    } catch (IOException e) {
      System.out.println(e);
    }
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
  
  public void processSine(double[] samples) {
    Channel_2 c = new Channel_2(28, 40, 80, 80, 8);
    for (double sample : samples) {
      c.processSignal(sample * 40 * 0.95);
    }
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
