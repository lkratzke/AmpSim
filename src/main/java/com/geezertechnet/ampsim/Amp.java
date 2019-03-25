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
