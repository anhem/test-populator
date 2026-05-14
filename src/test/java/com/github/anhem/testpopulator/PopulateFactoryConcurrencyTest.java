package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.OverridePopulate;
import com.github.anhem.testpopulator.config.OverrideTarget;
import com.github.anhem.testpopulator.config.PopulateConfig;
import com.github.anhem.testpopulator.model.circular.A;
import com.github.anhem.testpopulator.model.java.setter.Pojo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryConcurrencyTest {

    @Test
    void concurrentPopulateWithDifferentOverridesIsThreadSafe() throws InterruptedException, ExecutionException {
        int numberOfThreads = 50;
        int iterationsPerThread = 100;
        String globalValue = "global";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverride(String.class, () -> globalValue)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            String localValue = "local_" + i;
            tasks.add(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    String result = populateFactory.populate(String.class, String.class, () -> localValue);
                    if (!result.equals(localValue)) {
                        throw new IllegalStateException(String.format("Expected %s but got %s", localValue, result));
                    }

                    String globalResult = populateFactory.populate(String.class);
                    if (!globalResult.equals(globalValue)) {
                        throw new IllegalStateException(String.format("Expected %s but got %s", globalValue, globalResult));
                    }
                }
                return null;
            });
        }

        List<Future<Void>> futures = executorService.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }

        executorService.shutdown();
        assertThat(executorService.awaitTermination(1, TimeUnit.MINUTES)).isTrue();
        assertThat(populateFactory.populate(String.class)).isEqualTo(globalValue);
    }

    @Test
    void concurrentCircularDependencyDetectionIsThreadSafe() throws InterruptedException, ExecutionException {
        int numberOfThreads = 50;
        int iterationsPerThread = 20;
        PopulateConfig populateConfig = PopulateConfig.builder()
                .nullOnCircularDependency(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            tasks.add(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    A a = populateFactory.populate(A.class);
                    assertThat(a).isNotNull();
                    assertThat(a.getB()).isNotNull();
                }
                return null;
            });
        }

        List<Future<Void>> futures = executorService.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }
        executorService.shutdown();
        assertThat(executorService.awaitTermination(1, TimeUnit.MINUTES)).isTrue();
    }

    @Test
    void concurrentObjectFactoryUsageIsThreadSafe() throws InterruptedException, ExecutionException {
        int numberOfThreads = 20;
        int iterationsPerThread = 5;
        PopulateConfig populateConfig = PopulateConfig.builder()
                .objectFactoryEnabled(true)
                .build();
        PopulateFactory populateFactory = new PopulateFactory(populateConfig);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            tasks.add(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    String threadUniqueName = "thread_" + threadId + "_iter_" + j;
                    Map<Class<?>, OverridePopulate<?>> classOverrides = Map.of(String.class, (OverridePopulate<String>) () -> "value");
                    Map<OverrideTarget, OverridePopulate<?>> nameOverrides = Map.of(OverrideTarget.of(threadUniqueName, String.class), (OverridePopulate<String>) () -> "dummy");

                    Pojo pojo = populateFactory.populate(Pojo.class, classOverrides, nameOverrides);
                    assertThat(pojo).isNotNull();
                    assertThat(pojo.getStringValue()).isEqualTo("value");
                }
                return null;
            });
        }

        List<Future<Void>> futures = executorService.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }
        executorService.shutdown();
        assertThat(executorService.awaitTermination(1, TimeUnit.MINUTES)).isTrue();
    }
}
