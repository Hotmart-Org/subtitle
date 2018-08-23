package com.hotmart.subtitle.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AudioTranscriptionVTTConverter {

	private Integer maxCharCount;
	
	public AudioTranscriptionVTTConverter(Integer maxCharCount) {
		this.maxCharCount = maxCharCount;
	}

	public String format(AudioTranscription transcription) {
		StringBuilder srt = new StringBuilder("WEBVTT\n\n");
		int counter = 1;
		for (AudioTranscriptionChunk cue : transcription.getChunks()) {
			srt.append(counter++ + "\n");
			srt.append(VttUtils.formatTime(cue.getStartTime()) + " --> " + VttUtils.formatTime(cue.getEndTime()) + "\n");
			
			StringJoiner topLine = new StringJoiner(" ").setEmptyValue("");
			StringJoiner bottomLine = new StringJoiner(" ").setEmptyValue("");
			
			int wordCount = cue.getContent().size();
			
			StringJoiner current = topLine;
			for (int i = 0; i < wordCount; i++) {
				String word = cue.getContent().get(i);
				
				if(current == topLine && !(topLine.toString().length() + word.length() < maxCharCount || i < wordCount/2)) {
					current = bottomLine;
				}
				current.add(word);
			}
			
			srt.append(topLine + "\n");
			if(bottomLine.length() != 0) {
				srt.append(bottomLine + "\n");
			}
			srt.append("\n");
		}
		
		return srt.toString();
	}
	
	public AudioTranscription parse(String subtitle) throws AudioTranscriptionVTTException {
		try {
			AudioTranscription audioTranscription = new AudioTranscription();
			BufferedReader reader = new BufferedReader(new StringReader(subtitle));
			
			String line = reader.readLine(); //WEBVTT
			
			String timeRegex = "((\\d{2}:)?\\d{2}:\\d{2}\\.\\d{3})(\\s-->\\s)((\\d{2}:)?\\d{2}:\\d{2}\\.\\d{3})";
			Pattern pattern = Pattern.compile(timeRegex);

			while((line = reader.readLine()) != null) {
				if(line.trim().matches("\\d+")) {
					line = reader.readLine();
				}
				
				Matcher matcher = pattern.matcher(line.trim());

				if(matcher.matches()) {
					AudioTranscriptionChunk chunk = new AudioTranscriptionChunk();

					chunk.setStartTime(VttUtils.parseTime(matcher.group(1)));
					chunk.setEndTime(VttUtils.parseTime(matcher.group(4)));

					while (((line = reader.readLine()) != null) && (!line.isEmpty())) {
						chunk.getContent().add(line);
					}

					audioTranscription.addChunk(chunk);
				}
			}

			return audioTranscription;
		} catch (Exception e) {
			throw new AudioTranscriptionVTTException(e);
		}
	}
}
