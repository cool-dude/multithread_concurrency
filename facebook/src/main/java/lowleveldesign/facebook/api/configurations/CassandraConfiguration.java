package facebook.src.main.java.lowleveldesign.facebook.api.configurations;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@PropertySource(value = { "classpath:cassandra.yaml" })
@ConfigurationProperties("spring.data.cassandra")
@EnableCassandraRepositories
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${keyspace-name}")
    protected String keyspaceName;

    @Override
    protected String getKeyspaceName() {
        return this.keyspaceName;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(CreateKeyspaceSpecification
                .createKeyspace(keyspaceName).ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication());
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[] { "lowleveldesign.facebook.api.core.entities" };
    }

}
