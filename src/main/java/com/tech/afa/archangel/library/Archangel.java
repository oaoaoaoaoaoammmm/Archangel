package com.tech.afa.archangel.library;

import com.tech.afa.archangel.library.analyzer.Analyzer;
import com.tech.afa.archangel.library.analyzer.AnalyzerImpl;
import com.tech.afa.archangel.library.analyzer.StatisticService;
import com.tech.afa.archangel.library.config.ArchangelConfigurationProperties;
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
import com.tech.afa.archangel.library.schemaloader.SchemaLoader;
import com.tech.afa.archangel.library.utils.IdGenerator;
import com.tech.afa.archangel.library.wrapper.DataSourceWrapper;
import lombok.Getter;

import javax.sql.DataSource;

@Getter
public class Archangel {

    // TODO
    //  0) добавить проверку на функциональный индекс в TransformConditionAnalyzeWorker!??
    //  0.05) подумать над pg_statement над индексами по условию
    //  0.1) смотреть че там по оптимизации с join-ами
    //  0.2) 1 to 1 проверять
    //  0.22) анализатор на то что индекс нет для колонок с низкой селективностью например bool колонки
    //  0.23) А МБ ЕСТЬ СМЫСЛ В НАЧАЛЕ САМОМ ДЕЛАТЬ АНАЛИЗ АЛЯ ИНДЕКСЫ ПОКРЫВАЮТ ОДИНАКОВЫЕ СТРОКИ И ТД И ТП ПРИ СТАРТЕ ПРИЛОЖЕНИЯ
    //  0.3) прорегресить все что есть и посмотреть норм ли
    //  0.8) добавлять новые анализаторы
    //  1) проверить это все на конкретных приложениях
    //  2) привести в порядок тесты
    //  n) доделать инициализацию

    private final DataSource wrapperDataSource;

    private final ArchangelContext archangelContext;

    public Archangel(DataSource dataSource, ArchangelConfigurationProperties archangelProperties) {
        // Загрузка контекста
        SchemaRepository schemaRepository = new SchemaRepository(dataSource);
        SchemaLoader schemaLoader = new SchemaLoader(archangelProperties.getSchema(), schemaRepository);
        this.archangelContext = new ArchangelContext(archangelProperties.getDelayToRefreshSchemaSec(), schemaLoader);

        // Инициализация
        Parser parser = new ParserImpl();
        Exporter exporter = new LogExporter();
        IdGenerator idGenerator = new IdGenerator();
        StatisticService statisticService = new StatisticService(archangelContext);
        Analyzer analyzer = new AnalyzerImpl(dataSource, archangelContext);
        Processor processor = new ProcessorImpl(
            archangelProperties.getTriggerThreshold(),
            archangelProperties.getTriggerMode(),
            parser, exporter, analyzer, statisticService);
        Interceptor interceptor = new InterceptorImpl(processor, idGenerator);
        this.wrapperDataSource = new DataSourceWrapper(dataSource, interceptor);
    }

    public void forceRefreshSchema() {
        this.archangelContext.forceRefreshSchema();
    }
}
