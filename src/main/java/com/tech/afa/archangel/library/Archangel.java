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
import com.tech.afa.archangel.library.repository.SchemaRepository;
import com.tech.afa.archangel.library.schemaloader.ContextLoader;
import com.tech.afa.archangel.library.wrapper.DataSourceWrapper;
import lombok.Getter;

import javax.sql.DataSource;

@Getter
public class Archangel {

    private final String schema;

    private final DataSource originalDataSource;

    private final DataSource wrapperDataSource;

    public Archangel(DataSource dataSource, String schema) {
        this.schema = schema;
        this.originalDataSource = dataSource;

        // Загрузка контекста
        SchemaRepository schemaRepository = new SchemaRepository(dataSource);
        ContextLoader contextLoader = new ContextLoader(schemaRepository);
        ArchangelContext archangelContext = contextLoader.loadContext(schema);

        // Инициализация
        Parser parser = new ParserImpl();
        Exporter exporter = new LogExporter();
        Analyzer analyzer = new AnalyzerImpl(dataSource, archangelContext);
        Processor processor = new ProcessorImpl(parser, exporter, analyzer);
        Interceptor interceptor = new InterceptorImpl(processor);
        this.wrapperDataSource = new DataSourceWrapper(dataSource, interceptor);
    }
}
