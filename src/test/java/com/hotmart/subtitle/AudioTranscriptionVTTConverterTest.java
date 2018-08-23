package com.hotmart.subtitle;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.hotmart.subtitle.util.AudioTranscription;
import com.hotmart.subtitle.util.AudioTranscriptionChunk;
import com.hotmart.subtitle.util.AudioTranscriptionVTTConverter;
import com.hotmart.subtitle.util.AudioTranscriptionVTTException;

public class AudioTranscriptionVTTConverterTest {
	
	private AudioTranscriptionVTTConverter vttConverterService;
	
	@Before
	public void setup() {
		vttConverterService = new AudioTranscriptionVTTConverter(20);
	}
	
	@Test
	public void format_shouldNotBreakLine() {
		AudioTranscription transcription = new AudioTranscription();
		AudioTranscriptionChunk chunk = new AudioTranscriptionChunk();
		chunk.setStartTime(BigDecimal.ZERO);
		chunk.setEndTime(BigDecimal.TEN);
		transcription.addChunk(chunk);
		
		chunk.setContent(Arrays.asList("acabou","tudo"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nacabou tudo\n\n");
		
		chunk.setContent(Arrays.asList("sem","quebrar","linha"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nsem quebrar linha\n\n");
	}
	
	@Test
	public void format_shouldBreakLine() {
		AudioTranscription transcription = new AudioTranscription();
		AudioTranscriptionChunk chunk = new AudioTranscriptionChunk();
		chunk.setStartTime(BigDecimal.ZERO);
		chunk.setEndTime(BigDecimal.TEN);
		transcription.addChunk(chunk);
		
		chunk.setContent(Arrays.asList("fala","pessoal","para","gente","encurtar","aqui","o","nosso","caminho"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nfala pessoal para gente\nencurtar aqui o nosso caminho\n\n");
		
		chunk.setContent(Arrays.asList("entra","no","nosso","sentimento"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nentra no nosso\nsentimento\n\n");
		
		chunk.setContent(Arrays.asList("vai","quebrar","linha","sim"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nvai quebrar linha\nsim\n\n");
	}
	
	@Test
	public void parse_4Chunks() throws AudioTranscriptionVTTException {
		String subtitle = "WEBVTT\n\n1\n00:00:00.000 --> 00:00:03.355 \n<i>Anteriormente\nem \"Fear the Walking Dead\"...\n\n2\n00:00:03.456 --> 00:00:05.638 \nA terra que estão\nprecisa ser devolvida.\n\n3\n00:00:05.805 --> 00:00:07.686 \nAbandonem o rancho.\n\n4\n00:00:07.853 --> 00:00:10.188 \nSer um líder\né saber quando parar.\n";
		
		AudioTranscription transcription = vttConverterService.parse(subtitle);
		assertThat(transcription.getChunks()).size().isEqualTo(4);
		
		AudioTranscriptionChunk chunk = transcription.getChunks().get(0);
		assertThat(chunk.getStartTime()).isEqualTo(new BigDecimal("0.0"));
		assertThat(chunk.getEndTime()).isEqualTo(new BigDecimal("3.355"));
		assertThat(chunk.getContent()).containsExactly("<i>Anteriormente", "em \"Fear the Walking Dead\"...");
		
		chunk = transcription.getChunks().get(1);
		assertThat(chunk.getStartTime()).isEqualTo(new BigDecimal("3.456"));
		assertThat(chunk.getEndTime()).isEqualTo(new BigDecimal("5.638"));
		assertThat(chunk.getContent()).containsExactly("A terra que estão", "precisa ser devolvida.");
		
		chunk = transcription.getChunks().get(2);
		assertThat(chunk.getStartTime()).isEqualTo(new BigDecimal("5.805"));
		assertThat(chunk.getEndTime()).isEqualTo(new BigDecimal("7.686"));
		assertThat(chunk.getContent()).containsExactly("Abandonem o rancho.");
		
		chunk = transcription.getChunks().get(3);
		assertThat(chunk.getStartTime()).isEqualTo(new BigDecimal("7.853"));
		assertThat(chunk.getEndTime()).isEqualTo(new BigDecimal("10.188"));
		assertThat(chunk.getContent()).containsExactly("Ser um líder", "é saber quando parar.");
		
	}
	
}
