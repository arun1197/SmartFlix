/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speechtotext;
import javax.sound.sampled.*;
import java.io.*;
 

public class JavaSoundRecorder {
    // record duration, in milliseconds
 
    // path of the wav file
    File path = new File(System.getProperty("user.dir")+"/audio");
    File wavFile = new File(System.getProperty("user.dir")+"/audio/sound.wav");
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("- Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("- Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("- Finished");
    }
    
    public void record(int seconds) throws Exception{
        // creates a new thread that waits for a specified
        // of time before stopping
        createPath();
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(seconds*1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                finish();
            }
        });
 
        stopper.start();
 
        // start recording
        start();
    }
    
    public void createPath(){
        if (!path.exists() || !path.isDirectory()){
            path.mkdir();
        }
    }
}