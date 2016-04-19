/*
 * Copyright (C) 2016 Dmytro Shkil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shkil.android.util.concurrent;

import com.shkil.android.util.Result;
import com.shkil.android.util.ResultListener;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

public abstract class ResultFutureAdapter<W, V> implements ResultFuture<V> {

    private final ResultFuture<W> sourceFuture;
    private volatile Result<V> convertedResult;

    protected ResultFutureAdapter(ResultFuture<W> source) {
        if (source == null) {
            throw new NullPointerException();
        }
        this.sourceFuture = source;
    }

    @SuppressWarnings({"unchecked"})
    protected Result<V> handleResult(Result<W> result) {
        if (convertedResult != null) {
            return convertedResult;
        }
        synchronized (this) {
            if (convertedResult != null) {
                return convertedResult;
            }
            if (result.isInterrupted()) {
                return convertedResult = (Result<V>) result;
            }
            Exception exception = result.getException();
            if (exception != null) {
                Result<V> exceptionResult = processException(result);
                if (exceptionResult != null) {
                    return convertedResult = exceptionResult;
                }
            }
            try {
                V value = convertValue(result.getValue());
                return convertedResult = Result.success(value);
            }
            catch (Exception ex) {
                return Result.failure(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Result<V> processException(Result<W> result) {
        return (Result<V>) result;
    }

    protected abstract V convertValue(W value) throws Exception;

    @Override
    public boolean isResultReady() {
        return sourceFuture.isResultReady();
    }

    @Nullable
    @Override
    public Result<V> peekResult() {
        return handleResult(sourceFuture.peekResult());
    }

    @Override
    public Result<V> await() {
        return handleResult(sourceFuture.await());
    }

    @Override
    public Result<V> await(long timeout, TimeUnit unit) throws TimeoutException {
        return handleResult(sourceFuture.await(timeout, unit));
    }

    @Override
    public boolean cancel() {
        return sourceFuture.cancel();
    }

    @Override
    public boolean isCancelled() {
        return sourceFuture.isCancelled();
    }

    @Override
    public ResultFuture<V> setResultListener(final ResultListener<V> listener) {
        sourceFuture.setResultListener(new ResultListener<W>() {
            @Override
            public void onResult(Result<W> result) {
                listener.onResult(handleResult(result));
            }
        });
        return this;
    }

    @Override
    public ResultFuture<V> setResultListener(final ResultListener<V> listener, Executor resultExecutor) {
        sourceFuture.setResultListener(new ResultListener<W>() {
            @Override
            public void onResult(Result<W> result) {
                listener.onResult(handleResult(result));
            }
        }, resultExecutor);
        return this;
    }

}