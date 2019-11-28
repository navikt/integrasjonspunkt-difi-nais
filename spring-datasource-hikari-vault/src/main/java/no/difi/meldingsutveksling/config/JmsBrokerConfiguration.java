package no.difi.meldingsutveksling.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.store.jdbc.adapter.PostgresqlJDBCAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class JmsBrokerConfiguration {

	@Primary
	@Bean(initMethod = "start", destroyMethod = "stop")
	public BrokerService broker(@Qualifier("jmsDataSource") DataSource jmsDataSource,
								ActiveMQProperties activeMQProperties) throws Exception {
		final BrokerService broker = new BrokerService();
		broker.addConnector("vm://localhost");
		broker.setPersistent(true);
		JDBCPersistenceAdapter persistenceAdapter = new JDBCPersistenceAdapter();
		persistenceAdapter.setAdapter(new PostgresqlJDBCAdapter());
		persistenceAdapter.setDataSource(jmsDataSource);
		persistenceAdapter.setUseLock(false);
		persistenceAdapter.setCreateTablesOnStartup(true);
		broker.setPersistenceAdapter(persistenceAdapter);
		activeMQProperties.setBrokerUrl(broker.getVmConnectorURI().toString());
		return broker;
	}

}
