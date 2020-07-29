package com.hotmart.subtitle.util;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.Getter;

@Getter
public class AudioTranscription {

  private final Set<AudioTranscriptionChunk> chunks;

  public AudioTranscription() {
    chunks =
        new ConcurrentSkipListSet<>(Comparator.comparing(AudioTranscriptionChunk::getStartTime));
  }

  public void addChunk(AudioTranscriptionChunk chunk) {
    chunks.add(chunk);
  }
}
