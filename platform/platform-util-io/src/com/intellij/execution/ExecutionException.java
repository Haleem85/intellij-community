// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.execution;

import java.io.IOException;

public class ExecutionException extends Exception {
  public ExecutionException(final String s) {
    super(s);
  }

  public ExecutionException(final Throwable cause) {
    super(cause == null ? null : cause.getMessage(), cause);
  }

  public ExecutionException(final String s, Throwable cause) {
    super(s, cause);
  }

  public IOException toIOException() {
    final Throwable cause = getCause();
    return cause instanceof IOException ? (IOException)cause : new IOException(this);
  }
}