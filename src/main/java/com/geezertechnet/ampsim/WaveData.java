/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geezertechnet.ampsim;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a WAV file and interprets the data providing an array of samples and
 * a report containing the properties of the file. Provides getters for both raw 
 * byte[] values and converted (String/int) values. Two byte values are converted
 * to ints for convenience. File size limit is 2GB.
 * @author Loren Kratzke
 */
public class WaveData {
  // The header chunk
  private byte[] chunkId = new byte[4];          // 4 "RIFF"
  private byte[] chunkSize = new byte[4];        // 4 LittleEndian (size of file minus 8)
  private byte[] format = new byte[4];           // 4 "WAVE"
  
  // The format chunk
  private byte[] fmtChunkId = new byte[4];       // 4 "fmt "
  private byte[] fmtChunkSize = new byte[4];     // 4 LittleEndian (16 bytes for PCM)
  private byte[] fmtAudioFormat = new byte[2];   // 2 LittleEndian (1 for PCM data)
  private byte[] fmtNumChannels = new byte[2];   // 2 LittleEndian
  private byte[] fmtSampleRate = new byte[4];    // 4 LittleEndian
  private byte[] fmtByteRate = new byte[4];      // 4 LittleEndian
  private byte[] fmtBlockAlign = new byte[2];    // 2 LittleEndian
  private byte[] fmtBitsPerSample = new byte[2]; // 2 LittleEndian
  
  // The audio data chunk
  private byte[] dataChunkId = new byte[4];      // "data"
  private byte[] dataChunkSize = new byte[4];    // 4 LittleEndian
  private short[][] dataSamples;
  
  private List<Chunk> otherChunks = new ArrayList<>();
  
  public WaveData(String path) throws IOException {
    BufferedInputStream in = null;
    
    try {
      in = new BufferedInputStream(new FileInputStream(path));
      in.read(chunkId, 0, 4);
      in.read(chunkSize, 0, 4);
      in.read(format, 0, 4);

      // Here we read the chunks. Likely that the fmt chunk will be first
      // followed by zero or more other chunks, followed by the data chunk.
      // But order is not guaranteed so read them as they arrive.

      int bytesRead = 0;
      byte[] id = new byte[4];
      byte[] size = new byte[4];

      while ((bytesRead = in.read(id)) == 4) {
        in.read(size);
        String idString = new String(id);

        switch (idString) {
          case "fmt ":
            fmtChunkId = id;
            fmtChunkSize = size;
            in.read(fmtAudioFormat);
            in.read(fmtNumChannels);
            in.read(fmtSampleRate);
            in.read(fmtByteRate);
            in.read(fmtBlockAlign);
            in.read(fmtBitsPerSample);
            break;
          case "data":
            dataChunkId = id;
            dataChunkSize = size;
            
            // All this actually only works with 16 bit samples but does
            // support any number of channels. Not sure it works with 8 bit samples.
            // Definitely won't work with 24 bit samples. Could be modified to 
            // support 24/32 bit by using int[][] for dataSamples array and a few
            // other tweaks to this block and the Utils class (to convert byte[3] to int).
            int numChannels = getFmtNumChannelsInt();
            int bytesPerSample = getFmtBitsPerSampleInt()/8;
            int numSamples = getDataChunkSizeInt()/bytesPerSample;
            int numSamplesPerChannel = numSamples/numChannels;
            
            dataSamples = new short[numChannels][numSamplesPerChannel];
            
            byte[] buffer = new byte[bytesPerSample];
            int sample = 0;
            while (sample < numSamplesPerChannel) {
              for (int channel=0; channel<numChannels; channel++) {
                in.read(buffer);
                dataSamples[channel][sample] = Util.getLittleEndianShort(buffer);
              }
              sample++;
            }
            break;
          default:
            Chunk chunk = new Chunk();
            chunk.setId(id);
            chunk.setSize(size);
            byte[] data = new byte[Util.getLittleEndianInt(size)];
            in.read(data);
            chunk.setData(data);
            otherChunks.add(chunk);
            break;
        }
        id = new byte[4];
        size = new byte[4];
      }
    } finally {
      if (in != null) {
        try {in.close();} 
        catch (IOException e) {}
      }
    }
  }
  
  public String getReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("\nchunkId:          ");
    sb.append(getChunkIdString());
    sb.append("\nchunkSize:        ");
    sb.append(getChunkSizeInt());
    sb.append("\nformat:           ");
    sb.append(getFormatString());
    sb.append("\nfmtChunkId:       ");
    sb.append(getFmtChunkIdString());
    sb.append("\nfmtChunkSize:     ");
    sb.append(getFmtChunkSizeInt());
    sb.append("\nfmtAudioFormat:   ");
    sb.append(getFmtAudioFormatInt());
    sb.append("\nfmtNumChannels:   ");
    sb.append(getFmtNumChannelsInt());
    sb.append("\nfmtSampleRate:    ");
    sb.append(getFmtSampleRateInt());
    sb.append("\nfmtByteRate:      ");
    sb.append(getFmtByteRateInt());
    sb.append("\nfmtBlockAlign:    ");
    sb.append(getFmtBlockAlignInt());
    sb.append("\nfmtBitsPerSample: ");
    sb.append(getFmtBitsPerSampleInt());
    sb.append("\n");
    sb.append("\ndataChunkId:       ");
    sb.append(getDataChunkIdString());
    sb.append("\ndataChunkSize:     ");
    sb.append(getDataChunkSizeInt());
    
    // Taking a naive approach by just converting the chunk data into a String 
    // regardless of chunk type. This may/will result in garbage Strings for
    // less common chunk types. But we are probably safe for any WAVs ripped 
    // from a CDROM.
    for (Chunk chunk : otherChunks) {
      sb.append("\n");
      sb.append("\nchunkId:  ");
      sb.append(chunk.getIdString());
      sb.append("\nsize:     ");
      sb.append(chunk.getSizeInt());
      sb.append("\ndata:     ");
      sb.append(chunk.getDataString());
    }
    
    return sb.toString();
  }
  
  // Getters that return converted values (Strings and ints instead of byte[])
  
  public String getChunkIdString() {
    return new String(chunkId);
  }
  
  public int getChunkSizeInt() {
    return Util.getLittleEndianInt(chunkSize);
  }
  
  public String getFormatString() {
    return new String(format);
  }
  
  public String getFmtChunkIdString() {
    return new String(fmtChunkId);
  }

  public int getFmtChunkSizeInt() {
    return Util.getLittleEndianInt(fmtChunkSize);
  }

  // converting 2 byte value to int
  public int getFmtAudioFormatInt() {
    return Util.getLittleEndianInt(fmtAudioFormat);
  }

  // converting 2 byte value to int
  public int getFmtNumChannelsInt() {
    return Util.getLittleEndianInt(fmtNumChannels);
  }

  public int getFmtSampleRateInt() {
    return Util.getLittleEndianInt(fmtSampleRate);
  }

  public int getFmtByteRateInt() {
    return Util.getLittleEndianInt(fmtByteRate);
  }

  // converting 2 byte value to int
  public int getFmtBlockAlignInt() {
    return Util.getLittleEndianInt(fmtBlockAlign);
  }

  // converting 2 byte value to int
  public int getFmtBitsPerSampleInt() {
    return Util.getLittleEndianInt(fmtBitsPerSample);
  }

  public String getDataChunkIdString() {
    return new String(dataChunkId);
  }

  public int getDataChunkSizeInt() {
    return Util.getLittleEndianInt(dataChunkSize);
  }
  
  // Getters for raw byte[] values

  public byte[] getChunkId() {
    return chunkId;
  }

  public byte[] getChunkSize() {
    return chunkSize;
  }

  public byte[] getFormat() {
    return format;
  }

  public byte[] getFmtChunkId() {
    return fmtChunkId;
  }

  public byte[] getFmtChunkSize() {
    return fmtChunkSize;
  }

  public byte[] getFmtAudioFormat() {
    return fmtAudioFormat;
  }

  public byte[] getFmtNumChannels() {
    return fmtNumChannels;
  }

  public byte[] getFmtSampleRate() {
    return fmtSampleRate;
  }

  public byte[] getFmtByteRate() {
    return fmtByteRate;
  }

  public byte[] getFmtBlockAlign() {
    return fmtBlockAlign;
  }

  public byte[] getFmtBitsPerSample() {
    return fmtBitsPerSample;
  }

  public byte[] getDataChunkId() {
    return dataChunkId;
  }

  public byte[] getDataChunkSize() {
    return dataChunkSize;
  }

  public short[][] getDataSamples() {
    return dataSamples;
  }

  public List<Chunk> getOtherChunks() {
    return otherChunks;
  }
  
}

