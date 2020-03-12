package com.deferred.example.hammertime.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public abstract class AbstractController {
  public AbstractController() {
  }

  protected <T> DeferredResult<ResponseEntity<T>> executeDeferred(AbstractController.ThrowingSupplier<T> supplier) {
    DeferredResult<ResponseEntity<T>> deferred = new DeferredResult();
    Callable<ResponseEntity<T>> callable = () -> {
      try {
        deferred.setResult(supplier.get());
        return null;
      } catch (Exception var3) {
        deferred.setErrorResult(var3);
        return null;
      }
    };
    ForkJoinPool.commonPool().submit(callable);
    return deferred;
  }

  protected interface ThrowingSupplier<T> {
    ResponseEntity<T> get() throws Exception;
  }
}
