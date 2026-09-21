package com.pavithran.aichat.config;
import com.openai.client.OpenAIClient;import com.openai.client.okhttp.OpenAIOkHttpClient;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;
@Configuration public class OpenAiConfig { @Bean OpenAIClient openAIClient(){ return OpenAIOkHttpClient.fromEnv(); } }
