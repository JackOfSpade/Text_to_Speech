package com.amazonaws.demos.polly;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;

public class AmazonPolly
{

    private final AmazonPollyClient amazonPollyClient;
    private Voice voice;
    private static String text;
    private static String speed;
    BasicAWSCredentials credentials;

    public AmazonPolly(Region region, String selectedVoice)
    {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAIUEUIPQLNNXKXGWQ", "EBBnPUXuI/vzBenxWIrA07pr7fm2Wg/IoLsRmr0O");

        // create an Amazon Polly client in a specific region
        this.amazonPollyClient = new AmazonPollyClient(credentials, new ClientConfiguration());
        this.amazonPollyClient.setRegion(region);
        // Create describe voices request.
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

        // Synchronously ask Amazon Polly to describe available TTS voices.
        DescribeVoicesResult describeVoicesResult = this.amazonPollyClient.describeVoices(describeVoicesRequest);
        List<Voice> voiceList = describeVoicesResult.getVoices();
        for (Voice voice : voiceList)
        {
            if (voice.getId().equals(selectedVoice))
            {
                this.voice = voice;
                break;
            }
        }
    }

    public InputStream synthesize(String text, OutputFormat format) throws IOException
    {
        SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(this.voice.getId()).withOutputFormat(format).withTextType(TextType.Ssml);
        SynthesizeSpeechResult synthRes = this.amazonPollyClient.synthesizeSpeech(synthReq);

        return synthRes.getAudioStream();
    }

    public static String getText()
    {
        return text;
    }

    public static String getSpeed()
    {
        return speed;
    }

    public static void setText(String text)
    {
        AmazonPolly.text = text;
    }

    public static void setSpeed(String speed)
    {
        AmazonPolly.speed = speed;
    }

    public void setVoice(Voice voice)
    {
        this.voice = voice;
    }
}