package com.ldnhat.utils;

import java.io.File;
import java.io.Serializable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import javafx.concurrent.Task;

public class SoundRecorder extends Task<Void> implements Serializable {
	
	static final long RECORD_TIME = 60000;  // 1 minute

    // path of the wav file
    File wavFile = new File("D:\\intellij\\work-space\\ChatClientRMI\\record\\RecordAudio.wav");
    
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

	@Override
	protected Void call() throws Exception {
		// TODO Auto-generated method stub
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

        System.out.println("Start capturing...");

        AudioInputStream ais = new AudioInputStream(line);

        System.out.println("Start recording...");

        // start recording
        AudioSystem.write(ais, fileType, wavFile);
		return null;
	}
	
	AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    public void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    public File getWavFile() {
        return wavFile;
    }

    public void setWavFile(File wavFile) {
        this.wavFile = wavFile;
    }
}
