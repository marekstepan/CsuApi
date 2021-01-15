package com.tribes.csuapi.exceptions;

public class CsuApiException extends Exception {
  public CsuApiException(Throwable clause) {
    super("Houston we have a problem");
  }
}
