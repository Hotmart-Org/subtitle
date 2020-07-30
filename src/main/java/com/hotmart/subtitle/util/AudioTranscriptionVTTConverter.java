package com.hotmart.subtitle.util;

import java.util.StringJoiner;

public class AudioTranscriptionVTTConverter {

  private final Integer maxCharCount;

  public AudioTranscriptionVTTConverter(Integer maxCharCount) {
    this.maxCharCount = maxCharCount;
  }

  public String format(AudioTranscription transcription, long startPtsOffset) {
    StringBuilder srt = new StringBuilder("WEBVTT\n");
    if (startPtsOffset > 0L) {
      srt.append(String.format("X-TIMESTAMP-MAP=MPEGTS:%d,LOCAL:00:00:00.000%n", startPtsOffset));
    }
    srt.append("\n");
    int counter = 1;
    for (AudioTranscriptionChunk cue : transcription.getChunks()) {
      srt.append(counter++).append("\n");
      srt.append(VttUtils.formatTime(cue.getStartTime()))
          .append(" --> ")
          .append(VttUtils.formatTime(cue.getEndTime()))
          .append("\n");

      StringJoiner topLine = new StringJoiner(" ").setEmptyValue("");
      StringJoiner bottomLine = new StringJoiner(" ").setEmptyValue("");

      int wordCount = cue.getContent().size();

      StringJoiner current = topLine;
      for (int i = 0; i < wordCount; i++) {
        String word = cue.getContent().get(i);

        // == guarantees it is the same instance
        if (current == topLine
            && !(topLine.toString().length() + word.length() < maxCharCount || i < wordCount / 2)) {
          current = bottomLine;
        }
        current.add(word);
      }

      srt.append(topLine).append("\n");
      if (bottomLine.length() != 0) {
        srt.append(bottomLine).append("\n");
      }
      srt.append("\n");
    }

    return srt.toString();
  }
}
