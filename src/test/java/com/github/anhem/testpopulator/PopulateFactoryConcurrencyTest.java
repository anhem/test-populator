package com.github.anhem.testpopulator;

import com.github.anhem.testpopulator.config.PopulateConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class PopulateFactoryConcurrencyTest {

    @Test
    void concurrentPopulateWithDifferentOverridesIsThreadSafe() throws InterruptedException, ExecutionException {
        int numberOfThreads = 50;
        int iterationsPerThread = 100;
        String globalValue = "global";
        PopulateConfig populateConfig = PopulateConfig.builder()
                .addOverridePopulate(String.class, () -> globalValue)
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
}
