package com.tech.afa.archangel.library;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.analyzer.AnalyzerImpl;
import com.tech.afa.archangel.library.context.ArchangelContext;
import com.tech.afa.archangel.library.exporter.Exporter;
import com.tech.afa.archangel.library.exporter.LogExporter;
import com.tech.afa.archangel.library.interceptor.Interceptor;
import com.tech.afa.archangel.library.interceptor.InterceptorImpl;
import com.tech.afa.archangel.library.parser.Parser;
import com.tech.afa.archangel.library.parser.ParserImpl;
import com.tech.afa.archangel.library.processor.Processor;
import com.tech.afa.archangel.library.processor.ProcessorImpl;
import com.tech.afa.archangel.library.wrapper.DataSourceWrapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Archangel {

    private final DataSource originalDataSource;

    private final DataSource wrapperDataSource;

    public static Archangel initialize(DataSource dataSource) {
        Parser parser = new ParserImpl();
        Exporter exporter = new LogExporter();
        ArchangelContext archangelContext = new ArchangelContext();
        Analyzer analyzer = new AnalyzerImpl(dataSource, archangelContext);
        Processor processor = new ProcessorImpl(parser, exporter, analyzer);
        Interceptor interceptor = new InterceptorImpl(processor);
        DataSourceWrapper dataSourceWrapper = new DataSourceWrapper(dataSource, interceptor);
        return Archangel.builder()
            .originalDataSource(dataSource)
            .wrapperDataSource(dataSourceWrapper)
            .build();
    }
}
