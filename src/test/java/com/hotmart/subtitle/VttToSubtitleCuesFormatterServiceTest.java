package com.hotmart.subtitle;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotmart.subtitle.util.SubtitleCues;
import com.hotmart.subtitle.util.SubtitleCuesVTTConverter;
import com.hotmart.subtitle.util.SubtitleCuesVTTException;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class VttToSubtitleCuesFormatterServiceTest {

  private SubtitleCuesVTTConverter vttFormatterService;

  @Before
  public void setup() {
    vttFormatterService = new SubtitleCuesVTTConverter(0L);
  }

  @Test
  public void format() {
    SubtitleCues subtitleCues =
        SubtitleCues.builder()
            .cues(
                Arrays.asList(
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("0.2"))
                        .endTime(new BigDecimal("3.8"))
                        .content("Já sonhou trabalhar de forma\nindependente e para você?")
                        .build(),
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("4.05"))
                        .endTime(new BigDecimal("7.6"))
                        .content(
                            "Se a resposta é sim, saiba que muita\ngente também já pensou isso.")
                        .build()))
            .build();
    String expected =
        "WEBVTT\n\n"
            + "1\n00:00:00.200 --> 00:00:03.800\nJá sonhou trabalhar de forma\nindependente e para você?\n\n"
            + "2\n00:00:04.050 --> 00:00:07.600\nSe a resposta é sim, saiba que muita\ngente também já pensou isso.\n\n";

    String actual = vttFormatterService.format(subtitleCues, 0L);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void format_with_start_pts() {
    SubtitleCues subtitleCues =
        SubtitleCues.builder()
            .cues(
                Arrays.asList(
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("0.2"))
                        .endTime(new BigDecimal("3.8"))
                        .content("Já sonhou trabalhar de forma\nindependente e para você?")
                        .build(),
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("4.05"))
                        .endTime(new BigDecimal("7.6"))
                        .content(
                            "Se a resposta é sim, saiba que muita\ngente também já pensou isso.")
                        .build()))
            .build();
    String expected =
        "WEBVTT\n"
            + "X-TIMESTAMP-MAP=MPEGTS:1000,LOCAL:00:00:00.000\n\n"
            + "1\n00:00:00.200 --> 00:00:03.800\nJá sonhou trabalhar de forma\nindependente e para você?\n\n"
            + "2\n00:00:04.050 --> 00:00:07.600\nSe a resposta é sim, saiba que muita\ngente também já pensou isso.\n\n";

    String actual = vttFormatterService.format(subtitleCues, 1000L);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void parse() throws SubtitleCuesVTTException {
    String subtitle =
        "WEBVTT\n\n"
            + "1\n00:00:00.200 --> 00:00:03.800\nJá sonhou trabalhar de forma\nindependente e para você?\n\n"
            + "2\n00:00:04.050 --> 00:00:07.600\nSe a resposta é sim, saiba que muita\ngente também já pensou isso.\n\n";

    SubtitleCues expected =
        SubtitleCues.builder()
            .cues(
                Arrays.asList(
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("0.2"))
                        .endTime(new BigDecimal("3.8"))
                        .content("Já sonhou trabalhar de forma\nindependente e para você?")
                        .build(),
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("4.05"))
                        .endTime(new BigDecimal("7.6"))
                        .content(
                            "Se a resposta é sim, saiba que muita\ngente também já pensou isso.")
                        .build()))
            .build();

    SubtitleCues actual = vttFormatterService.parse(subtitle);
    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void parse_ignore_start_pts() throws SubtitleCuesVTTException {
    String subtitle =
        "WEBVTT\n"
            + "X-TIMESTAMP-MAP=MPEGTS:900000,LOCAL:00:00:00.000\n\n"
            + "1\n00:00:00.200 --> 00:00:03.800\nJá sonhou trabalhar de forma\nindependente e para você?\n\n"
            + "2\n00:00:04.050 --> 00:00:07.600\nSe a resposta é sim, saiba que muita\ngente também já pensou isso.\n\n";

    SubtitleCues expected =
        SubtitleCues.builder()
            .cues(
                Arrays.asList(
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("0.2"))
                        .endTime(new BigDecimal("3.8"))
                        .content("Já sonhou trabalhar de forma\nindependente e para você?")
                        .build(),
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("4.05"))
                        .endTime(new BigDecimal("7.6"))
                        .content(
                            "Se a resposta é sim, saiba que muita\ngente também já pensou isso.")
                        .build()))
            .build();

    SubtitleCues actual = vttFormatterService.parse(subtitle);
    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void parseWithPartialTimestamp() throws SubtitleCuesVTTException {
    String subtitle =
        "WEBVTT\n\n"
            + "1\n00:00.200 --> 00:03.800\nJá sonhou trabalhar de forma\nindependente e para você?\n\n"
            + "2\n00:04.050 --> 00:07.600\nSe a resposta é sim, saiba que muita\ngente também já pensou isso.\n\n";

    SubtitleCues expected =
        SubtitleCues.builder()
            .cues(
                Arrays.asList(
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("0.2"))
                        .endTime(new BigDecimal("3.8"))
                        .content("Já sonhou trabalhar de forma\nindependente e para você?")
                        .build(),
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("4.05"))
                        .endTime(new BigDecimal("7.6"))
                        .content(
                            "Se a resposta é sim, saiba que muita\ngente também já pensou isso.")
                        .build()))
            .build();

    SubtitleCues actual = vttFormatterService.parse(subtitle);
    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }

  @Test
  public void parseWithoutCueNumbers() throws SubtitleCuesVTTException {
    String subtitle =
        "WEBVTT\n\n"
            + "00:00.200 --> 00:03.800\nJá sonhou trabalhar de forma\nindependente e para você?\n\n"
            + "00:04.050 --> 00:07.600\nSe a resposta é sim, saiba que muita\ngente também já pensou isso.\n\n";

    SubtitleCues expected =
        SubtitleCues.builder()
            .cues(
                Arrays.asList(
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("0.2"))
                        .endTime(new BigDecimal("3.8"))
                        .content("Já sonhou trabalhar de forma\nindependente e para você?")
                        .build(),
                    SubtitleCues.Cue.builder()
                        .startTime(new BigDecimal("4.05"))
                        .endTime(new BigDecimal("7.6"))
                        .content(
                            "Se a resposta é sim, saiba que muita\ngente também já pensou isso.")
                        .build()))
            .build();

    SubtitleCues actual = vttFormatterService.parse(subtitle);
    assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
  }
}
