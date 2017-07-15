/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speechtotext;

import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author DevSingh
 */
public class SpeechConverter {
    public String convert() throws Exception {
        // Instantiates a client
        SpeechClient speech = SpeechClient.create();

        // The path to the audio file to transcribe
        String fileName = System.getProperty("user.dir")+"/audio/sound.wav";

        // Reads the audio file into memory
        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);
        ByteString audioBytes = ByteString.copyFrom(data);
        
        System.out.println("- Converting...");
        // Builds the sync recognize request
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        // Performs speech recognition on the audio file
        RecognizeResponse response = speech.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();
        StringBuilder sb = new StringBuilder();
        for (SpeechRecognitionResult result: results) {
            List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
            for (SpeechRecognitionAlternative alternative: alternatives) {
                sb.append(alternative.getTranscript());
//                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        }
        System.out.println(sb);
        speech.close();
        return sb.toString();
    }
}
