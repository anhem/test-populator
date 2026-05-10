package com.github.anhem.testpopulator.model.java;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiverseJavaTypes {

    private StringBuilder stringBuilder;
    private StringBuffer stringBuffer;
    private Throwable throwable;
    private Exception exception;
    private RuntimeException runtimeException;
    private Error error;
    private PriorityQueue<String> priorityQueue;
    private Scanner scanner;
    private Stream<String> stream;
    private IntStream intStream;
    private LongStream longStream;
    private DoubleStream doubleStream;
    private Iterator<String> iterator;
    private Iterable<String> iterable;
    private AtomicInteger atomicInteger;
    private AtomicLong atomicLong;
    private AtomicBoolean atomicBoolean;
    private CompletableFuture<String> completableFuture;
    private Future<String> future;
    private Class<?> clazz;
    private Stream<Integer> streamInteger;
    private PriorityQueue<Integer> priorityQueueInteger;
    private Iterator<Boolean> iteratorBoolean;
    private Iterable<Long> iterableLong;
    private CompletableFuture<Integer> completableFutureInteger;
    private Future<Boolean> futureBoolean;

    public static DiverseJavaTypes of(
            StringBuilder stringBuilder,
            StringBuffer stringBuffer,
            Throwable throwable,
            Exception exception,
            RuntimeException runtimeException,
            Error error,
            PriorityQueue<String> priorityQueue,
            Scanner scanner,
            Stream<String> stream,
            IntStream intStream,
            LongStream longStream,
            DoubleStream doubleStream,
            Iterator<String> iterator,
            Iterable<String> iterable,
            AtomicInteger atomicInteger,
            AtomicLong atomicLong,
            AtomicBoolean atomicBoolean,
            CompletableFuture<String> completableFuture,
            Future<String> future,
            Class<?> clazz,
            Stream<Integer> streamInteger,
            PriorityQueue<Integer> priorityQueueInteger,
            Iterator<Boolean> iteratorBoolean,
            Iterable<Long> iterableLong,
            CompletableFuture<Integer> completableFutureInteger,
            Future<Boolean> futureBoolean
    ) {
        return new DiverseJavaTypes(
                stringBuilder,
                stringBuffer,
                throwable,
                exception,
                runtimeException,
                error,
                priorityQueue,
                scanner,
                stream,
                intStream,
                longStream,
                doubleStream,
                iterator,
                iterable,
                atomicInteger,
                atomicLong,
                atomicBoolean,
                completableFuture,
                future,
                clazz,
                streamInteger,
                priorityQueueInteger,
                iteratorBoolean,
                iterableLong,
                completableFutureInteger,
                futureBoolean
        );

    }

}
