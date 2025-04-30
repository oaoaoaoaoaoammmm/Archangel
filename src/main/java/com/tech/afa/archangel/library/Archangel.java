package com.tech.afa.archangel.library;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.analyzer.AnalyzerImpl;
import com.tech.afa.archangel.library.analyzer.StatisticService;
import com.tech.afa.archangel.library.config.TriggerMode;
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
import com.tech.afa.archangel.library.utils.IdGenerator;
import com.tech.afa.archangel.library.wrapper.DataSourceWrapper;
import lombok.Getter;

import javax.sql.DataSource;

@Getter
public class Archangel {

    // TODO
    //  0) научиться TriggerMode делать чтоб статистика считалась, а больше ничего не делалось не экспорт не анализ полноценный
    //  0.5) сделать чтобы возвращались копии объектов статистики
    //  1) проверить это все на конкретных приложениях
    //  n) доделать инициализацию

    private final String schema;

    private final DataSource originalDataSource;

    private final DataSource wrapperDataSource;

    public Archangel(DataSource dataSource, String schema, int triggerThreshold, TriggerMode triggerMode) {
        this.schema = schema;
        this.originalDataSource = dataSource;

        // Загрузка контекста
        SchemaRepository schemaRepository = new SchemaRepository(dataSource);
        ContextLoader contextLoader = new ContextLoader(schemaRepository);
        ArchangelContext archangelContext = contextLoader.loadContext(schema);

        // Инициализация
        Parser parser = new ParserImpl();
        Exporter exporter = new LogExporter();
        IdGenerator idGenerator = new IdGenerator();
        StatisticService statisticService = new StatisticService(archangelContext);
        Analyzer analyzer = new AnalyzerImpl(dataSource, archangelContext);
        Processor processor = new ProcessorImpl(triggerThreshold, TriggerMode.BY_COUNT, parser, exporter, analyzer, statisticService);
        Interceptor interceptor = new InterceptorImpl(processor, idGenerator);
        this.wrapperDataSource = new DataSourceWrapper(dataSource, interceptor);
    }
}
