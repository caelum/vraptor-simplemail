package br.com.caelum.vraptor.simplemail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ExecutorServiceProvider{

	private ExecutorService pool;

	@PostConstruct
	public void initialize() {
		this.pool = Executors.newCachedThreadPool();
	}

	@Produces
	public ExecutorService getInstance() {
		return this.pool;
	}

	@PreDestroy
	public void close() {
		this.pool.shutdown();
	}

}
