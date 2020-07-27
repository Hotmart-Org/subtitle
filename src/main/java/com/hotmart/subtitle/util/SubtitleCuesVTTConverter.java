package com.hotmart.subtitle.util;

import com.hotmart.subtitle.util.SubtitleCues.Cue;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleCuesVTTConverter {

  public String format(SubtitleCues subtitleCues, long startPtsOffset) {
    StringBuilder srt = new StringBuilder("WEBVTT\n");
    if (startPtsOffset > 0) {
      srt.append(String.format("X-TIMESTAMP-MAP=MPEGTS:%d,LOCAL:00:00:00.000%n", startPtsOffset));
    }
    srt.append("\n"); // empty line separator.
    int counter = 1;
    for (Cue cue : subtitleCues.getCues()) {
      srt.append(counter++).append("\n");
      srt.append(VttUtils.formatTime(cue.getStartTime()))
          .append(" --> ")
          .append(VttUtils.formatTime(cue.getEndTime()))
          .append("\n");
      srt.append(cue.getContent());
      srt.append("\n\n");
    }

    return srt.toString();
  }

  public SubtitleCues parse(String subtitle) throws SubtitleCuesVTTException {
    try (BufferedReader reader = new BufferedReader(new StringReader(subtitle));) {
      SubtitleCues subtitleCues = new SubtitleCues();

      String ignored = reader.readLine();
      String line; // WEBVTT

      String timeRegex =
          "((\\d{2}:)?\\d{2}:\\d{2}\\.\\d{3})(\\s-->\\s)((\\d{2}:)?\\d{2}:\\d{2}\\.\\d{3})";
      Pattern pattern = Pattern.compile(timeRegex);

      while ((line = reader.readLine()) != null) {
        // parser will ignore x-timestamp-map header
        if (line.trim().matches("X-TIMESTAMP-MAP=MPEGTS:\\d+,LOCAL:00:00:00.000")){
          line = reader.readLine();
        }
        if (line.trim().matches("\\d+")) {
          line = reader.readLine();
        }

        Matcher matcher = pattern.matcher(line.trim());

        if (matcher.matches()) {
          subtitleCues.getCues().add(parseCue(reader, matcher));
        }
      }

      return subtitleCues;
    } catch (Exception e) {
      throw new SubtitleCuesVTTException(e);
    }
  }

  private SubtitleCues.Cue parseCue(BufferedReader reader, Matcher matcher) {
    SubtitleCues.Cue cue = new SubtitleCues.Cue();

    cue.setStartTime(VttUtils.parseTime(matcher.group(1)));
    cue.setEndTime(VttUtils.parseTime(matcher.group(4)));

    StringBuilder content = new StringBuilder();

    Iterator<String> it = reader.lines().iterator();

    int lines = 0;
    while (it.hasNext()) {
      String line = it.next();

      if (!line.isEmpty()) {
        if (lines == 0) {
          content.append(line);
        } else {
          content.append("\n").append(line);
        }

        lines++;
      } else {
        break;
      }
    }

    cue.setContent(content.toString());

    return cue;
  }
}
