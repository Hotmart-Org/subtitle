/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleWriter;
import fr.noop.subtitle.util.SubtitleTimeCode;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/** Created by clebeaupin on 11/10/15. */
public class VttWriter implements SubtitleWriter {
  private final String charset; // Charset used to encode file
  private final Logger log = Logger.getLogger(this.getClass());

  public VttWriter(String charset) {
    this.charset = charset;
  }

  public void write(SubtitleObject subtitleObject, long startPtsOffset, OutputStream os)
      throws IOException {
    try {
      // Write header
      os.write("WEBVTT\n".getBytes(this.charset));
      if (startPtsOffset > 0) {
        log.debug("[Subtitle] Writing X-TIMESTAMP-MAP header with value: " + startPtsOffset);
        os.write(
            String.format("X-TIMESTAMP-MAP=MPEGTS:%d,LOCAL:00:00:00.000%n", startPtsOffset)
                .getBytes(this.charset));
      }
      os.write("\n".getBytes(this.charset));
      writeCues(subtitleObject, os);
    } catch (UnsupportedEncodingException e) {
      throw new IOException("Encoding error in input subtitle");
    }
  }

  @Override
  public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
    try {
      // Write header
      os.write("WEBVTT\n\n".getBytes(this.charset));

      writeCues(subtitleObject, os);
    } catch (UnsupportedEncodingException e) {
      throw new IOException("Encoding error in input subtitle");
    }
  }

  private void writeCues(SubtitleObject subtitleObject, OutputStream os) throws IOException {
    // Write cues
    for (SubtitleCue cue : subtitleObject.getCues()) {
      if (cue.getId() != null) {
        // Write number of subtitle
        String number = String.format("%s%n", cue.getId());
        os.write(number.getBytes(this.charset));
      }

      // Write Start time and end time
      String startToEnd =
          String.format(
              "%s --> %s %n",
              this.formatTimeCode(cue.getStartTime()), this.formatTimeCode(cue.getEndTime()));
      os.write(startToEnd.getBytes(this.charset));

      // Write text
      String text = String.format("%s%n", cue.getText());
      os.write(text.getBytes(this.charset));

      // Write empty line
      os.write("\n".getBytes(this.charset));
    }
  }

  private String formatTimeCode(SubtitleTimeCode timeCode) {
    return String.format(
        "%02d:%02d:%02d.%03d",
        timeCode.getHour(), timeCode.getMinute(), timeCode.getSecond(), timeCode.getMillisecond());
  }
}
