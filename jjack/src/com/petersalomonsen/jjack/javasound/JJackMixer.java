package com.petersalomonsen.jjack.javasound;

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixer
 * Version: 0.3
 *
 * Date:    2007-04-09
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Control.Type;

import de.gulden.framework.jjack.JJackAudioEvent;
import de.gulden.framework.jjack.JJackClient;
import de.gulden.framework.jjack.JJackSystem;

/**
 * A Javasound Mixer implementation that enables use of jack through this standard java interface..
 * 
 * @author Peter Johan Salomonsen
 */
public class JJackMixer extends JJackClient implements Mixer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Currently opened target/source lines
	 */
	Vector<TargetJJackLine> targetLines = new Vector<TargetJJackLine>();
	Vector<SourceJJackLine> sourceLines = new Vector<SourceJJackLine>();
	
	/**
	 * Supported audio formats
	 */
	AudioFormat[] audioFormatsOut;
	AudioFormat[] audioFormatsIn;
	
	public JJackMixer()
	{	
		/**
		 * Determine number of available out channels
		 */
		int outputs = JJackSystem.countPorts(JJackSystem.OUTPUT);		
		audioFormatsOut = new AudioFormat[8*(outputs)];
		fillAudioFormats(audioFormatsOut);
		
		/**
		 * Determine number of available in channels
		 */
		int inputs = JJackSystem.countPorts(JJackSystem.OUTPUT);		
		audioFormatsIn = new AudioFormat[8*(inputs)];
		fillAudioFormats(audioFormatsIn);
		
		try
		{
			JJackSystem.setProcessor(this);
		} catch(Exception e)
		{
		}

	}
	
	/**
	 * Fill audioFormats array with available audio formats. We'll support 8,16,24 and 32 bit, big and little endian and lines up to "inputs/outputs"
	 * number of channels
	 * @param audioFormats
	 */
	private void fillAudioFormats(AudioFormat[] audioFormats)
	{
		for(int n=0;n<audioFormats.length;n++)
			audioFormats[n] = new AudioFormat(JJackSystem.getSampleRate(),8+(8*(n%4)),((n/8)+1),true,((n%8)/4) == 0 ? false : true);
	}
	
	@Override
	public void process(JJackAudioEvent e) {
		
		int channelIndex = 0;
		
		for(SourceJJackLine line : sourceLines)
		{
			int channels = line.getFormat().getChannels();
			int length = e.getOutput().capacity()*channels;
			
			float[] lineBuffer = line.readFloat(length);

			for(int n=0;n<length;n++)
			{
				e.getOutputs()[(n%channels)+channelIndex].put((n/channels)+channelIndex, lineBuffer[n]);				
			}
			channelIndex += channels;
		}
		
		channelIndex = 0;
		
		for(TargetJJackLine line : targetLines)
		{
			int channels = line.getFormat().getChannels();
			int length = e.getOutput().capacity()*channels;
			
			float[] lineBuffer = line.getFloatBuffer(length);

			for(int n=0;n<length;n++)
			{
				lineBuffer[n] =  e.getOutputs()[(n%channels)+channelIndex].get((n/channels)+channelIndex);				
			}
			channelIndex += channels;
		}
	}

	public Line getLine(javax.sound.sampled.Line.Info info)
			throws LineUnavailableException {
		
		assert(info.getLineClass() == SourceJJackLine.class || info.getLineClass() == TargetJJackLine.class);
		
		try {
			Line line = (Line)info.getLineClass().newInstance();
			if(line.getClass()==SourceJJackLine.class)
				sourceLines.add((SourceJJackLine)line);
			return line; 
		} catch (InstantiationException e) {
			throw new LineUnavailableException();
		} catch (IllegalAccessException e) {
			throw new LineUnavailableException();
		}
	}

	public int getMaxLines(javax.sound.sampled.Line.Info info) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Info getMixerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info[] getSourceLineInfo() {
		
		return new Line.Info[] {
				new Line.Info(SourceJJackLine.class)
		};
	}

	public javax.sound.sampled.Line.Info[] getSourceLineInfo(
			javax.sound.sampled.Line.Info info) {
		
		return new Line.Info[] {
				new Line.Info(SourceJJackLine.class)
		};
	}

	public Line[] getSourceLines() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info[] getTargetLineInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info[] getTargetLineInfo(
			javax.sound.sampled.Line.Info info) {
		// TODO Auto-generated method stub
		return null;
	}

	public Line[] getTargetLines() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLineSupported(javax.sound.sampled.Line.Info info) {
		// TODO Implement proper checking
		return true;
	}

	public boolean isSynchronizationSupported(Line[] lines, boolean maintainSync) {
		// TODO Auto-generated method stub
		return false;
	}

	public void synchronize(Line[] lines, boolean maintainSync) {
		// TODO Auto-generated method stub

	}

	public void unsynchronize(Line[] lines) {
		// TODO Auto-generated method stub

	}

	public void addLineListener(LineListener listener) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public Control getControl(Type control) {
		// TODO Auto-generated method stub
		return null;
	}

	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info getLineInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isControlSupported(Type control) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void open() throws LineUnavailableException {
		// TODO Auto-generated method stub

	}

	public void removeLineListener(LineListener listener) {
		// TODO Auto-generated method stub

	}

}
