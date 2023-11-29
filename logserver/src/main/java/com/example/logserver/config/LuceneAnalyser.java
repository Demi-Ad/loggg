package com.example.logserver.config;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class LuceneAnalyser implements LuceneAnalysisConfigurer {
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer("logger_analyzer")
                .custom()
                .tokenizer("standard")
                .tokenFilter("lowercase")
                .tokenFilter("stop")
                .tokenFilter("trim")
                .charFilter("htmlStrip");

        context.normalizer("keyword_normalizer")
                .custom()
                .tokenFilter("lowercase")
                .tokenFilter("stop")
                .charFilter("htmlStrip");

    }
}
